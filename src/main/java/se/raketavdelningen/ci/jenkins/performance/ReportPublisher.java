package se.raketavdelningen.ci.jenkins.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import se.raketavdelningen.ci.jenkins.performance.action.ReportBuildAction;
import se.raketavdelningen.ci.jenkins.performance.action.ReportProjectAction;
import se.raketavdelningen.ci.jenkins.performance.aggregator.Aggregator;
import se.raketavdelningen.ci.jenkins.performance.aggregator.TimeBasedAggregator;
import se.raketavdelningen.ci.jenkins.performance.exception.ReportException;
import se.raketavdelningen.ci.jenkins.performance.group.LabelSampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.group.SampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.parser.JMeterCSVParser;
import se.raketavdelningen.ci.jenkins.performance.parser.ReportParser;
import se.raketavdelningen.ci.jenkins.performance.report.Report;
import se.raketavdelningen.ci.jenkins.performance.report.ReportEntry;
import se.raketavdelningen.ci.jenkins.performance.report.ReportLog;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedSample;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;
import se.raketavdelningen.ci.jenkins.performance.sample.SamplesList;
import se.raketavdelningen.ci.jenkins.performance.sample.SamplesMap;

public class ReportPublisher extends Recorder {

    private Aggregator aggregator;

    private SampleGroupFunction groupFunction;

    private static final String filePattern = "**/*.csv";
    
    private boolean containsHeader;
    
    private boolean printToBuildLog;
    
    private boolean saveBuildResults;
    
    @DataBoundConstructor
    public ReportPublisher() throws JAXBException {
        this.aggregator = new TimeBasedAggregator();
        this.groupFunction = new LabelSampleGroupFunction();
        this.containsHeader = true;
        this.printToBuildLog = false;
        this.saveBuildResults = true;
    }

    @Extension
    public static class PerformanceReportPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return ReportConstants.PLUGIN_NAME;
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public Collection<Action> getProjectActions(AbstractProject<?, ?> project) {
        Object build = project.getLastSuccessfulBuild();
        if (build != null) {
            ReportBuildAction action = project.getLastSuccessfulBuild().getAction(ReportBuildAction.class);
            return Collections.<Action>singleton(new ReportProjectAction(action.getReport()));
        }
        return Collections.<Action>emptyList();
    }
    
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        PrintStream logger = listener.getLogger();
        if (build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            logger.println("Skipping parsing JMeter result files since result is equal to or worse than Failure");
            return true;
        }                
        
        List<FilePath> files = findAllPerformanceReports(build.getWorkspace(), filePattern);
        SamplesMap samples = new SamplesMap();
        
        for (FilePath file : files) {
            parseFile(logger, samples, file);
        }
        
        Report report = getPerformanceReportToUpdate(build, listener);
        ReportBuildAction action = new ReportBuildAction(samples, report);
        Set<String> keys = samples.keySet();
        for (String key : keys) {
            ReportEntry entry = handleSamplesByKey(build, logger, samples, key);
            addEntryToLog(report, key, entry);
        }        
        build.addAction(action);
        
        return true;
    }

    private void addEntryToLog(Report report, String key,
            ReportEntry entry) {
        ReportLog log = report.getReportLog(key);
        if (log != null) {
            log.addEntry(entry);
        } else {
            log = new ReportLog(key);
            log.addEntry(entry);
            report.addLog(log);
        }
    }

    private Report getPerformanceReportToUpdate(AbstractBuild<?, ?> build, BuildListener listener) {
        Report report;
        Run<?,?> previousSuccessfulBuild = build.getPreviousSuccessfulBuild();
        if (previousSuccessfulBuild != null) {
            ReportBuildAction action = previousSuccessfulBuild.getAction(ReportBuildAction.class);
            report = action.getReport();
            if (report == null) {
                report = new Report();
            }
        } else {
            report = new Report();
        }
        return report;
    }
    
    private ReportEntry handleSamplesByKey(AbstractBuild<?, ?> build,
            PrintStream logger,
            SamplesMap aggregatedSamples,
            String key) {
        List<AggregatedSample> samples = aggregatedSamples.get(key);
        if (printToBuildLog) {
            printToBuildLog(key, samples, logger);
        }
        if (saveBuildResults) {
            saveResultsToFile(key, samples, build.getRootDir().getAbsolutePath(), logger);
        }
        return new ReportEntry(
                build.number, 
                findMin(samples), 
                calculateAverage(samples), 
                findMax(samples), 
                calculateNrOfSamples(samples),
                calculateNrOfFailures(samples),
                isSuccess(samples));
    }

    private long calculateNrOfFailures(List<AggregatedSample> samples) {
        long totalNrOfFailures = 0;
        for (AggregatedSample sample : samples) {
            totalNrOfFailures+=sample.getNrOfSamples();
        }
        return totalNrOfFailures;
    }

    private long calculateNrOfSamples(List<AggregatedSample> samples) {
        long totalNrOfSamples = 0;
        for (AggregatedSample sample : samples) {
            totalNrOfSamples+=sample.getNrOfSamples();
        }
        return totalNrOfSamples;
    }

    private long calculateAverage(List<AggregatedSample> samples) {
        long totalAverage = 0;
        for (AggregatedSample sample : samples) {
            totalAverage+=sample.getAverage();
        }
        return totalAverage / (samples.size());
    }
    
    private boolean isSuccess(List<AggregatedSample> samples) {
        for (AggregatedSample sample : samples) {
            if (!sample.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    private long findMin(List<AggregatedSample> samples) {
        long min = Long.MAX_VALUE;
        for (AggregatedSample sample : samples) {
            if (sample.getMin() < min) {
                min = sample.getMin();
            }
        }
        return min;
    }

    private long findMax(List<AggregatedSample> samples) {
        long max = Long.MIN_VALUE;
        for (AggregatedSample sample : samples) {
            if (max < sample.getMax()) {
                max = sample.getMax();
            }
        }
        return max;
    }
    
    private void parseFile(PrintStream logger,
            SamplesMap samples,
            FilePath file) {
        logger.format("Parsing file %1$s%n", file.getName());
        ReportParser parser = new JMeterCSVParser(file, containsHeader);
        Sample sample = parser.getNextSample();
        aggregator.initializeAggregatorFromFirstSample(sample);
        while (sample != null) {
            if (isInNewAggregationPeriod(sample)) {
            	aggregateAndStartNewPeriod(samples);
            }
            groupFunction.addSampleToGroup(sample);
            sample = parser.getNextSample();
        }
        aggregateAndStartNewPeriod(samples);
        logger.format("Done parsing file %1s%n", file.getName());
    }

    private boolean isInNewAggregationPeriod(Sample sample) {
    	return !aggregator.isSampleInCurrentAggregation(sample);
	}

	private void aggregateAndStartNewPeriod(
            SamplesMap aggregatedSamples) {
        Set<String> groupKeys = groupFunction.getKeys();
        aggregateSamples(aggregatedSamples, groupKeys);
        groupFunction.clearGroups();
        aggregator.startNewAggregationPeriod();
    }

    private void saveResultsToFile(String key,
            List<AggregatedSample> samples, String absolutePath, PrintStream logger) {
    	final String fileName = "performance_report_" + key + ".csv";
    	final String format = "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s%n";
    	logger.format("Writing results to %1$s/%2$s%n", absolutePath, fileName);
    	try (
    			FileWriter writer = new FileWriter(new File(absolutePath, fileName)); 
    			Formatter formatter = new Formatter(writer)) {
    		for (AggregatedSample sample : samples) {
    			formatter.format(format, sample.getTimestamp(), sample.getMin(), sample.getAverage(), sample.getMax(), sample.getNrOfSamples(), sample.isSuccess());
    		}
		} catch (IOException e) {
			logger.format("Error occured when writing file %1$, message is %2$%n", fileName, e.getMessage());
			throw new ReportException(e);
		}
    }

    private void printToBuildLog(String key,
            List<AggregatedSample> samples, PrintStream logger) {
        logger.format("Result for %1$s%n",  key);
        logger.format("|%1$8s|%2$6s|%3$6s|%4$6s|%5$6s|%6$7s|%n", "Time", "Min", "Avg.", "Max", "Nr", "Success");
        for (AggregatedSample sample : samples) {
            logger.format("|%1$8s|%2$6s|%3$6s|%4$6s|%5$6s|%6$7s|%n",
                    DateFormatUtils.format(sample.getTimestamp(), DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern()), 
                    sample.getMin(), sample.getAverage(), sample.getMax(), sample.getNrOfSamples(), sample.isSuccess());
        }
    }

    private void aggregateSamples(
            SamplesMap aggregatedSamples,
            Set<String> groupKeys) {
        for (String key : groupKeys) {
            List<Sample> samples = groupFunction.getSamples(key);
            AggregatedSample aggregatedSample = aggregator.aggregatePerformanceSamples(samples, key);
            addSampleToKey(aggregatedSample, key, aggregatedSamples);
        }
    }

    private void addSampleToKey(AggregatedSample aggregatedSample,
            String key,
            SamplesMap aggregatedSamples) {
        SamplesList samples = aggregatedSamples.get(key);
        if (samples == null) {
            samples = new SamplesList();
            aggregatedSamples.put(key, samples);
        }
        samples.add(aggregatedSample);
    }

    /**
     * Finds all performance reports according to the given pattern in the current workspace
     * @param workspace
     * @param filePattern
     * @return
     * @throws ReportException
     */
    private List<FilePath> findAllPerformanceReports(FilePath workspace, String filePattern) 
            throws ReportException {
        List<FilePath> files = new ArrayList<FilePath>();
        String parts[] = filePattern.split("\\s*[;:,]+\\s*");
        try {
            for (String path : parts) {
                FilePath[] filePaths = workspace.list(path);
                if (filePaths.length > 0) {
                    files.addAll(Arrays.asList(filePaths));
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new ReportException(e);
        }
        return files;
    }
}
