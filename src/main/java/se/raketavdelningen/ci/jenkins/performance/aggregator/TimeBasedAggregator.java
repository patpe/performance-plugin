package se.raketavdelningen.ci.jenkins.performance.aggregator;

import java.util.List;

import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedPerformanceSample;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;
import hudson.model.Descriptor;

/**
 * Aggregator that aggregates samples using time as the unit.
 *
 */
public class TimeBasedAggregator extends Aggregator {

    private int minutesPerAggregation = 1;
    
    private long nextTimestamp = -1;
    
    @Override
    public boolean isSampleInCurrentAggregation(PerformanceSample sample) {
        return (nextTimestamp < sample.getTimestamp());
    }

    @Override
    public AggregatedPerformanceSample aggregatePerformanceSamples(
            List<PerformanceSample> samples, String key) {
        long aggregatedTimestamp = calculateAggregatedTimestamp(samples);
        long samplesCounter = 0;
        long sumOfElapsed = 0;
        boolean success = true;
        
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (PerformanceSample sample : samples) {
            long elapsed = sample.getElapsed();
            if (max < elapsed) {
                max = elapsed;
            }
            if (elapsed < min) {
                min = elapsed;
            }
            
            sumOfElapsed+=sample.getElapsed();
            success = (success && sample.isSuccess());
            samplesCounter++;
        }
        
        long averageElapsed = sumOfElapsed / samplesCounter;
        
        return new AggregatedPerformanceSample(aggregatedTimestamp, max, min, averageElapsed, 
                success, samplesCounter, key);
    }

    private long calculateAggregatedTimestamp(List<PerformanceSample> samples) {
        long firstTimestamp = samples.get(0).getTimestamp();
        long lastTimestamp = samples.get(samples.size()).getTimestamp();
        return (lastTimestamp + firstTimestamp) / 2;
    }

    @Override
    public void startNewAggregationPeriod() {
        this.nextTimestamp = this.nextTimestamp + (minutesPerAggregation * 60 * 1000);
    }

    @Override
    public Descriptor<Aggregator> getDescriptor() {
        return new Descriptor<Aggregator>() {    
        
            @Override
            public String getDisplayName() {
                return "Time Based Aggregator";
            }
        };
    }
}
