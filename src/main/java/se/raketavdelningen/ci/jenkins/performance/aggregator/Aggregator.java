package se.raketavdelningen.ci.jenkins.performance.aggregator;

import hudson.model.Describable;

import java.util.List;

import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedSample;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

/**
 * An aggregator is used to aggregate samples, thereby reducing the number of sample points
 *
 */
public abstract class Aggregator implements Describable<Aggregator> {
    
    public abstract void initializeAggregatorFromFirstSample(Sample sample);
    
    public abstract void startNewAggregationPeriod();
    
    public abstract boolean isSampleInCurrentAggregation(Sample sample);
    
    public abstract AggregatedSample aggregatePerformanceSamples(List<Sample> samples, String key);
    
    protected final int[] calculateResponseTimeDistribution(List<Sample> samples) {
        int[] result = new int[10];
        long max = findMaxInSamples(samples);
        long[] percentileValues = calculatePercentileValues(max);
        
        for (Sample sample : samples) {
            int percentileIndex = findPercentileForSample(sample, percentileValues);
            result[percentileIndex]++;
        }
        return result;
    }

    private int findPercentileForSample(Sample sample,
            long[] percentileValues) {
        long elapsed = sample.getElapsed();
        for (int percentileIndex = 0; percentileIndex < percentileValues.length; percentileIndex++) {
            if (elapsed >= percentileValues[percentileIndex]) {
                return percentileIndex;
            }
        }
        return 10;
    }

    private long[] calculatePercentileValues(long max) {
        long percentile10 = (long) (max * 0.1);
        long percentile20 = (long) (max * 0.2);
        long percentile30 = (long) (max * 0.3);
        long percentile40 = (long) (max * 0.4);
        long percentile50 = (long) (max * 0.5);
        long percentile60 = (long) (max * 0.6);
        long percentile70 = (long) (max * 0.7);
        long percentile80 = (long) (max * 0.8);
        long percentile90 = (long) (max * 0.9);
        
        long[] percentileValues = new long[10];
        percentileValues[0] = percentile10;
        percentileValues[1] = percentile20;
        percentileValues[2] = percentile30;
        percentileValues[3] = percentile40;
        percentileValues[4] = percentile50;
        percentileValues[5] = percentile60;
        percentileValues[6] = percentile70;
        percentileValues[7] = percentile80;
        percentileValues[8] = percentile90;
        percentileValues[9] = max;
        return percentileValues;
    }    

    private long findMaxInSamples(List<Sample> samples) {
        long max = Long.MIN_VALUE;
        for (Sample sample : samples) {
            if (max < sample.getElapsed()) {
                max = sample.getElapsed();
            }
        }
        return max;
    }
}
