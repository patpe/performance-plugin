package se.raketavdelningen.ci.jenkins.performance.aggregator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

public class TimeBasedAggregatorTest {

    Aggregator aggregator = new TimeBasedAggregator();
    
    @Test
    public void testIsSampleInCurrentAggregation() {
        List<PerformanceSample> samples = initializeSampleListWithOneMinuteBetweenSamples(2);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        for (PerformanceSample sample : samples) {
            assertTrue(aggregator.isSampleInCurrentAggregation(sample));
        }
    }
    
    @Test
    public void testInCurrentAggregationPeriodWithSamePeriod() {
        List<PerformanceSample> samples = initializeSampleListWithNoTimeDifference(10);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        for (PerformanceSample sample : samples) {
            assertTrue(aggregator.isSampleInCurrentAggregation(sample));
        }
    }
    
    @Test
    public void testInCurrentAggregationPeriodWithSamplesEndingJustBeforeNextPeriod() {
        List<PerformanceSample> samples = initializeSampleList(2, 59);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        for (PerformanceSample sample : samples) {
            assertTrue(aggregator.isSampleInCurrentAggregation(sample));
        }
    }
    
    @Test
    public void testInCurrentAggregationPeriodWithSamplesEndingJustAfterNextPeriod() {
        List<PerformanceSample> samples = initializeSampleList(2, 61);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        assertTrue(aggregator.isSampleInCurrentAggregation(samples.get(0)));
        samples.remove(0);
        for (PerformanceSample sample : samples) {
            assertFalse(aggregator.isSampleInCurrentAggregation(sample));
        }
    }

    @Test
    public void testInCurrentAggregationPeriodWithMultipleSamples() {
        List<PerformanceSample> samples = initializeSampleList(100, 70);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        assertTrue(aggregator.isSampleInCurrentAggregation(samples.get(0)));
        samples.remove(0);
        for (PerformanceSample sample : samples) {
            assertFalse(aggregator.isSampleInCurrentAggregation(sample));
            aggregator.startNewAggregationPeriod();
        }
    }
    
    private List<PerformanceSample> initializeSampleListWithNoTimeDifference(int nrOfSamples) {
        return initializeSampleList(nrOfSamples, 0);
    }
    
    private List<PerformanceSample> initializeSampleListWithOneMinuteBetweenSamples(int nrOfSamples) {
        return initializeSampleList(nrOfSamples, 60);
    }
    
    private List<PerformanceSample> initializeSampleList(int nrOfSamples, int secondsBetweenSamples) {
        List<PerformanceSample> samples = new ArrayList<>(nrOfSamples);
        long currentTimestamp = System.currentTimeMillis();
        for (int i = 0; i < nrOfSamples; i++) {
            currentTimestamp = currentTimestamp + i * secondsBetweenSamples * 1000;
            samples.add(new PerformanceSample(currentTimestamp, 100, true, 1000, "label", "url"));
        }
        return samples;
    }
}
