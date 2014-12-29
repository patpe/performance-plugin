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
}
