package se.raketavdelningen.ci.jenkins.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import se.raketavdelningen.ci.jenkins.performance.aggregator.Aggregator;
import se.raketavdelningen.ci.jenkins.performance.aggregator.TimeBasedAggregator;
import se.raketavdelningen.ci.jenkins.performance.group.LabelSampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.group.SampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.parser.JMeterCSVParser;
import se.raketavdelningen.ci.jenkins.performance.parser.PerformanceReportParser;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedPerformanceSample;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

public class PerformanceReportPublisher extends Recorder {

    private Aggregator aggregator;

    private SampleGroupFunction groupFunction;

    private static final String filePattern = "**/*.csv";
    
    private boolean containsHeader;
    
    private boolean printToBuildLog;
    
    private boolean saveAsXmlResults;

    @DataBoundConstructor
    public PerformanceReportPublisher() {
        this.aggregator = new TimeBasedAggregator();
        this.groupFunction = new LabelSampleGroupFunction();
        this.containsHeader = true;
        this.printToBuildLog = true;
        this.saveAsXmlResults = false;
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
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        List<FilePath> files = findAllPerformanceReports(build.getWorkspace(), filePattern);
        Map<String, List<AggregatedPerformanceSample>> aggregatedSamples = new HashMap<>();
        
        for (FilePath file : files) {
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

        Set<String> keys = aggregatedSamples.keySet();
        for (String key : keys) {
            List<AggregatedPerformanceSample> samples = aggregatedSamples.get(key);
            if (printToBuildLog) {
                printToBuildLog(key, samples, logger);
            }
            if (saveAsXmlResults) {
                saveXmlResults(key, samples, build.getRootDir().getAbsolutePath());
            }
        }
                
        // TODO Add the performance action to the current build
        return true;
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

    private void saveXmlResults(String key,
            List<AggregatedPerformanceSample> samples, String absolutePath) {
    }

    private void printToBuildLog(String key,
            List<AggregatedPerformanceSample> samples, PrintStream logger) {
        logger.format("Result for %1$s%n",  key);
        logger.format("|Time|Min|Avg.|Max|Nr|%n");
        for (AggregatedPerformanceSample sample : samples) {
            logger.format("|%1$s|%2$s|%3$s|%4$s|%5$s|%n",
                    DateFormatUtils.format(sample.getTimestamp(), DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern()), 
                    sample.getMin(), sample.getAverage(), sample.getMax(), sample.getNrOfSamples());
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
     * Finds all 
     * @param workspace
     * @param filePattern
     * @return
     * @throws InterruptedException 
     * @throws IOException
     */
    private List<FilePath> findAllPerformanceReports(FilePath workspace, String filePattern) throws IOException, InterruptedException {
        List<FilePath> files = new ArrayList<FilePath>();
        String parts[] = filePattern.split("\\s*[;:,]+\\s*");
        for (String path : parts) {
            FilePath[] filePaths = workspace.list(path);
            if (filePaths.length > 0) {
                files.addAll(Arrays.asList(filePaths));
            }
        }
        return files;
    }
}
