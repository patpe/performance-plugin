package se.raketavdelningen.ci.jenkins.performance.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.exception.PerformanceReportException;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedPerformanceSample;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

/**
 * Test class for {@link TimeBasedAggregator}
 * 
 * @author Patrik
 */
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
    
    @Test
    public void testAggregateSamples() {
    	List<PerformanceSample> samples = initializeSampleListWithNoTimeDifference(100);
    	AggregatedPerformanceSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	assertEquals("key", sample.getSampleToken());
    	assertEquals(50l, sample.getAverage());
    	assertEquals(1l, sample.getMin());
    	assertEquals(100l, sample.getMax());
    	assertEquals(100l, sample.getNrOfSamples());
    	
    	PerformanceSample firstSample = samples.get(0);
    	long averageTime = firstSample.getTimestamp();
    	assertEquals(averageTime, sample.getTimestamp());
    }
    
    @Test
    public void testAggregateSample() {
    	List<PerformanceSample> samples = initializeSampleListWithNoTimeDifference(1);
    	AggregatedPerformanceSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	assertEquals("key", sample.getSampleToken());
    	assertEquals(1l, sample.getAverage());
    	assertEquals(1l, sample.getMin());
    	assertEquals(1l, sample.getMax());
    	assertEquals(1l, sample.getNrOfSamples());
    	
    	PerformanceSample firstSample = samples.get(0);
    	long averageTime = firstSample.getTimestamp();
    	assertEquals(averageTime, sample.getTimestamp());
    }

    @Test
    public void testAggregateSamplesWithDifferentTimestamps() {
    	List<PerformanceSample> samples = new ArrayList<>(2);
    	PerformanceSample firstSample = new PerformanceSample(1000l, 1, true, 1, "label", "url");
    	PerformanceSample secondSample = new PerformanceSample(2000l, 1, true, 1, "label", "url");
    	PerformanceSample thirdSample = new PerformanceSample(3000l, 1, true, 1, "label", "url");
    	samples.add(firstSample);
    	samples.add(secondSample);
    	samples.add(thirdSample);
    	AggregatedPerformanceSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	
    	assertEquals(2000l, sample.getTimestamp());
    }
    
    @Test(expected = PerformanceReportException.class)
    public void testAggregateEmptyList() {
    	List<PerformanceSample> samples = initializeSampleList(0, 0);
    	aggregator.aggregatePerformanceSamples(samples, "key");
    }
    
    @Test
    public void testGetDescriptor() {
    	assertNotNull(aggregator.getDescriptor());
    	assertNotNull(aggregator.getDescriptor().getDisplayName());
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
            samples.add(new PerformanceSample(currentTimestamp, i + 1, true, 1000, "label", "url"));
        }
        return samples;
    }
}
