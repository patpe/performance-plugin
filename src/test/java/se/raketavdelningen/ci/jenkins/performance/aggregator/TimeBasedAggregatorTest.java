package se.raketavdelningen.ci.jenkins.performance.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.exception.ReportException;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedSample;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

/**
 * Test class for {@link TimeBasedAggregator}
 * 
 * @author Patrik
 */
public class TimeBasedAggregatorTest {

    Aggregator aggregator = new TimeBasedAggregator();
    
    @Test
    public void testIsSampleInCurrentAggregation() {
        List<Sample> samples = initializeSampleListWithOneMinuteBetweenSamples(2);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        for (Sample sample : samples) {
            assertTrue(aggregator.isSampleInCurrentAggregation(sample));
        }
    }
    
    @Test
    public void testInCurrentAggregationPeriodWithSamePeriod() {
        List<Sample> samples = initializeSampleListWithNoTimeDifference(10);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        for (Sample sample : samples) {
            assertTrue(aggregator.isSampleInCurrentAggregation(sample));
        }
    }
    
    @Test
    public void testInCurrentAggregationPeriodWithSamplesEndingJustBeforeNextPeriod() {
        List<Sample> samples = initializeSampleList(2, 59);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        for (Sample sample : samples) {
            assertTrue(aggregator.isSampleInCurrentAggregation(sample));
        }
    }
    
    @Test
    public void testInCurrentAggregationPeriodWithSamplesEndingJustAfterNextPeriod() {
        List<Sample> samples = initializeSampleList(2, 61);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        assertTrue(aggregator.isSampleInCurrentAggregation(samples.get(0)));
        samples.remove(0);
        for (Sample sample : samples) {
            assertFalse(aggregator.isSampleInCurrentAggregation(sample));
        }
    }

    @Test
    public void testInCurrentAggregationPeriodWithMultipleSamples() {
        List<Sample> samples = initializeSampleList(100, 70);
        aggregator.initializeAggregatorFromFirstSample(samples.get(0));
        assertTrue(aggregator.isSampleInCurrentAggregation(samples.get(0)));
        samples.remove(0);
        for (Sample sample : samples) {
            assertFalse(aggregator.isSampleInCurrentAggregation(sample));
            aggregator.startNewAggregationPeriod();
        }
    }
    
    @Test
    public void testAggregateSamples() {
    	List<Sample> samples = initializeSampleListWithNoTimeDifference(100);
    	AggregatedSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	assertEquals("key", sample.getSampleToken());
    	assertEquals(50l, sample.getAverage());
    	assertEquals(1l, sample.getMin());
    	assertEquals(100l, sample.getMax());
    	assertEquals(100l, sample.getNrOfSamples());
    	assertTrue(sample.isSuccess());
    	
    	Sample firstSample = samples.get(0);
    	long averageTime = firstSample.getTimestamp();
    	assertEquals(averageTime, sample.getTimestamp());
    }
    
    @Test
    public void testAggregateSamplesWithOneFailedSample() {
    	List<Sample> samples = initializeSampleListWithNoTimeDifference(100);
    	samples.get(99).setSuccess(false);
    	AggregatedSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	assertFalse(sample.isSuccess());
    }
    
    @Test
    public void testAggregateSample() {
    	List<Sample> samples = initializeSampleListWithNoTimeDifference(1);
    	AggregatedSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	assertEquals("key", sample.getSampleToken());
    	assertEquals(1l, sample.getAverage());
    	assertEquals(1l, sample.getMin());
    	assertEquals(1l, sample.getMax());
    	assertEquals(1l, sample.getNrOfSamples());
    	assertTrue(sample.isSuccess());
    	
    	Sample firstSample = samples.get(0);
    	long averageTime = firstSample.getTimestamp();
    	assertEquals(averageTime, sample.getTimestamp());
    }

    @Test
    public void testAggregateSamplesWithDifferentTimestamps() {
    	List<Sample> samples = new ArrayList<>(2);
    	Sample firstSample = new Sample(1000l, 1, true, 1, "label", "url");
    	Sample secondSample = new Sample(2000l, 1, true, 1, "label", "url");
    	Sample thirdSample = new Sample(3000l, 1, true, 1, "label", "url");
    	samples.add(firstSample);
    	samples.add(secondSample);
    	samples.add(thirdSample);
    	AggregatedSample sample = aggregator.aggregatePerformanceSamples(samples, "key");
    	
    	assertEquals(2000l, sample.getTimestamp());
    }
    
    @Test(expected = ReportException.class)
    public void testAggregateEmptyList() {
    	List<Sample> samples = initializeSampleList(0, 0);
    	aggregator.aggregatePerformanceSamples(samples, "key");
    }
    
    @Test
    public void testGetDescriptor() {
    	assertNotNull(aggregator.getDescriptor());
    	assertNotNull(aggregator.getDescriptor().getDisplayName());
    }
    
    private List<Sample> initializeSampleListWithNoTimeDifference(int nrOfSamples) {
        return initializeSampleList(nrOfSamples, 0);
    }
    
    private List<Sample> initializeSampleListWithOneMinuteBetweenSamples(int nrOfSamples) {
        return initializeSampleList(nrOfSamples, 60);
    }
    
    private List<Sample> initializeSampleList(int nrOfSamples, int secondsBetweenSamples) {
        List<Sample> samples = new ArrayList<>(nrOfSamples);
        long currentTimestamp = System.currentTimeMillis();
        for (int i = 0; i < nrOfSamples; i++) {
            currentTimestamp = currentTimestamp + i * secondsBetweenSamples * 1000;
            samples.add(new Sample(currentTimestamp, i + 1, true, 1000, "label", "url"));
        }
        return samples;
    }
}
