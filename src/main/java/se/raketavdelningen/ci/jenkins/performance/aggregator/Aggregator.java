package se.raketavdelningen.ci.jenkins.performance.aggregator;

import hudson.model.Describable;

import java.util.List;

import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedPerformanceSample;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

/**
 * An aggregator is used to aggregate samples, thereby reducing the number of sample points
 *
 */
public abstract class Aggregator implements Describable<Aggregator> {
    
    public abstract void startNewAggregationPeriod();
    
    public abstract boolean isSampleInCurrentAggregation(PerformanceSample sample);
    
    public abstract AggregatedPerformanceSample aggregatePerformanceSamples(List<PerformanceSample> samples, String key);    
}
