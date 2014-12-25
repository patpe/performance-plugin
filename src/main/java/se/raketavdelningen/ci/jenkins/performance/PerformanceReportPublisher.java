package se.raketavdelningen.ci.jenkins.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.time.DateFormatUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import se.raketavdelningen.ci.jenkins.performance.aggregator.Aggregator;
import se.raketavdelningen.ci.jenkins.performance.aggregator.TimeBasedAggregator;
import se.raketavdelningen.ci.jenkins.performance.exception.PerformanceReportException;
import se.raketavdelningen.ci.jenkins.performance.group.LabelSampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.group.SampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.parser.JMeterCSVParser;
import se.raketavdelningen.ci.jenkins.performance.parser.PerformanceReportParser;
import se.raketavdelningen.ci.jenkins.performance.report.PerformanceBuildEntry;
import se.raketavdelningen.ci.jenkins.performance.report.PerformanceBuildReport;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedPerformanceSample;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

public class PerformanceReportPublisher extends Recorder {

    private Aggregator aggregator;

    private SampleGroupFunction groupFunction;

    private static final String filePattern = "**/*.csv";
    
    private boolean containsHeader;
    
    private boolean printToBuildLog;
    
    private boolean saveBuildResults;
    
    private JAXBContext jaxbCtx;

    @DataBoundConstructor
    public PerformanceReportPublisher() throws JAXBException {
        this.aggregator = new TimeBasedAggregator();
        this.groupFunction = new LabelSampleGroupFunction();
        this.containsHeader = true;
        this.printToBuildLog = false;
        this.saveBuildResults = true;        
        this.jaxbCtx = JAXBContext.newInstance(PerformanceBuildReport.class);
    }

    @Extension
    public static class PerformanceReportPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return PerformanceReportConstants.PLUGIN_NAME;
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        PrintStream logger = listener.getLogger();
        List<FilePath> files = findAllPerformanceReports(build.getWorkspace(), filePattern);
        Map<String, List<AggregatedPerformanceSample>> aggregatedSamples = new HashMap<>();
        
        for (FilePath file : files) {
            parseFile(logger, aggregatedSamples, file);
        }

        PerformanceBuildReport report = getPerformanceBuildReportToUpdate(build);
        Set<String> keys = aggregatedSamples.keySet();
        for (String key : keys) {
            PerformanceBuildEntry entry = handleSamplesByKey(build, logger, aggregatedSamples, key);
            report.addEntry(entry);
        }
        persistBuildReport(report, build.getRootDir());
        
        // TODO Add the performance action to the current build
        return true;
    }

    private PerformanceBuildReport getPerformanceBuildReportToUpdate(AbstractBuild<?, ?> build) {
        PerformanceBuildReport report;
        try {
            Run<?,?> previousSuccessfulBuild = build.getPreviousSuccessfulBuild();
            if (previousSuccessfulBuild != null) {
                File previousBuildDirectory = previousSuccessfulBuild.getRootDir();
                File performanceBuildLog = new File(previousBuildDirectory, "performance_build.xml");
                if (performanceBuildLog.exists()) {
                    InputStream is = new FileInputStream(performanceBuildLog);
                    Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                    report = (PerformanceBuildReport) unmarshaller.unmarshal(is);
                } else {
                    report = new PerformanceBuildReport();
                }
            } else {
                report = new PerformanceBuildReport();
            }
        } catch (FileNotFoundException | JAXBException e) {
            throw new PerformanceReportException(e);
        }
        return report;
    }
    
    private void persistBuildReport(PerformanceBuildReport report, File currentBuildDir) {
        File performanceBuildLog = new File(currentBuildDir, "performance_build.xml");
        try {
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(report, performanceBuildLog);
        } catch (JAXBException e) {
            throw new PerformanceReportException(e);
        }
    }

    private PerformanceBuildEntry handleSamplesByKey(AbstractBuild<?, ?> build,
            PrintStream logger,
            Map<String, List<AggregatedPerformanceSample>> aggregatedSamples,
            String key) {
        List<AggregatedPerformanceSample> samples = aggregatedSamples.get(key);
        if (printToBuildLog) {
            printToBuildLog(key, samples, logger);
        }
        if (saveBuildResults) {
            saveResultsToFile(key, samples, build.getRootDir().getAbsolutePath(), logger);
        }
        return new PerformanceBuildEntry(build.number, key, findMin(samples), calculateAverage(samples), findMax(samples), isSuccess(samples));
    }

    private long calculateAverage(List<AggregatedPerformanceSample> samples) {
        long totalAverage = 0;
        for (AggregatedPerformanceSample sample : samples) {
            totalAverage+=sample.getAverage();
        }
        return totalAverage / (samples.size());
    }
    
    private boolean isSuccess(List<AggregatedPerformanceSample> samples) {
        for (AggregatedPerformanceSample sample : samples) {
            if (!sample.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    private long findMin(List<AggregatedPerformanceSample> samples) {
        long min = Long.MAX_VALUE;
        for (AggregatedPerformanceSample sample : samples) {
            if (sample.getMin() < min) {
                min = sample.getMin();
            }
        }
        return min;
    }

    private long findMax(List<AggregatedPerformanceSample> samples) {
        long max = Long.MIN_VALUE;
        for (AggregatedPerformanceSample sample : samples) {
            if (max < sample.getMax()) {
                max = sample.getMax();
            }
        }
        return max;
    }
    
    private void parseFile(PrintStream logger,
            Map<String, List<AggregatedPerformanceSample>> aggregatedSamples,
            FilePath file) {
        logger.format("Parsing file %1$s%n", file.getName());
        PerformanceReportParser parser = new JMeterCSVParser(file, containsHeader);
        PerformanceSample sample = parser.getNextSample();
        aggregator.initializeAggregatorFromFirstSample(sample);
        while (sample != null) {
            if (isInNewAggregationPeriod(sample)) {
            	aggregateAndStartNewPeriod(aggregatedSamples);
            }
            groupFunction.addSampleToGroup(sample);
            sample = parser.getNextSample();
        }
        aggregateAndStartNewPeriod(aggregatedSamples);
        logger.format("Done parsing file %1s%n", file.getName());
    }

    private boolean isInNewAggregationPeriod(PerformanceSample sample) {
    	return !aggregator.isSampleInCurrentAggregation(sample);
	}

	private void aggregateAndStartNewPeriod(
            Map<String, List<AggregatedPerformanceSample>> aggregatedSamples) {
        Set<String> groupKeys = groupFunction.getKeys();
        aggregateSamples(aggregatedSamples, groupKeys);
        groupFunction.clearGroups();
        aggregator.startNewAggregationPeriod();
    }

    private void saveResultsToFile(String key,
            List<AggregatedPerformanceSample> samples, String absolutePath, PrintStream logger) {
    	final String fileName = "performance_report_" + key + ".csv";
    	final String format = "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s%n";
    	logger.format("Writing results to %1$s/%2$s%n", absolutePath, fileName);
    	try (
    			FileWriter writer = new FileWriter(new File(absolutePath, fileName)); 
    			Formatter formatter = new Formatter(writer)) {
    		for (AggregatedPerformanceSample sample : samples) {
    			formatter.format(format, sample.getTimestamp(), sample.getMin(), sample.getAverage(), sample.getMax(), sample.getNrOfSamples(), sample.isSuccess());
    		}
		} catch (IOException e) {
			logger.format("Error occured when writing file %1$, message is %2$%n", fileName, e.getMessage());
			throw new PerformanceReportException(e);
		}
    }

    private void printToBuildLog(String key,
            List<AggregatedPerformanceSample> samples, PrintStream logger) {
        logger.format("Result for %1$s%n",  key);
        logger.format("|%1$8s|%2$6s|%3$6s|%4$6s|%5$6s|%6$7s|%n", "Time", "Min", "Avg.", "Max", "Nr", "Success");
        for (AggregatedPerformanceSample sample : samples) {
            logger.format("|%1$8s|%2$6s|%3$6s|%4$6s|%5$6s|%6$7s|%n",
                    DateFormatUtils.format(sample.getTimestamp(), DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern()), 
                    sample.getMin(), sample.getAverage(), sample.getMax(), sample.getNrOfSamples(), sample.isSuccess());
        }
    }

    private void aggregateSamples(
            Map<String, List<AggregatedPerformanceSample>> aggregatedSamples,
            Set<String> groupKeys) {
        for (String key : groupKeys) {
            List<PerformanceSample> samples = groupFunction.getSamples(key);
            AggregatedPerformanceSample aggregatedSample = aggregator.aggregatePerformanceSamples(samples, key);
            addSampleToKey(aggregatedSample, key, aggregatedSamples);
        }
    }

    private void addSampleToKey(AggregatedPerformanceSample aggregatedSample,
            String key,
            Map<String, List<AggregatedPerformanceSample>> aggregatedSamples) {
        List<AggregatedPerformanceSample> samples = aggregatedSamples.get(key);
        if (samples == null) {
            samples = new ArrayList<>();
            aggregatedSamples.put(key, samples);
        }
        samples.add(aggregatedSample);
    }

    /**
     * Finds all performance reports according to the given pattern in the current workspace
     * @param workspace
     * @param filePattern
     * @return
     * @throws PerformanceReportException
     */
    private List<FilePath> findAllPerformanceReports(FilePath workspace, String filePattern) 
            throws PerformanceReportException {
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
            throw new PerformanceReportException(e);
        }
        return files;
    }
}
