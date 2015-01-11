package se.raketavdelningen.ci.jenkins.performance.sample;

import static org.junit.Assert.*;

import org.junit.Test;

public class SamplesDistributionTest {

    @Test
    public void testSamplesDistribution() {
        SamplesDistribution dist = new SamplesDistribution(10);
        assertEquals(1, dist.getPercentile10());
        assertEquals(3, dist.getPercentile30());
        assertEquals(7, dist.getPercentile70());
        assertEquals(9, dist.getPercentile90());
        assertEquals(10, dist.getMax());
    }

    @Test
    public void testAddSampleToDistribution() {
        SamplesDistribution dist = new SamplesDistribution(10);
        for (int i = 0; i <= 10; i++) {
            dist.addSampleToDistribution(new Sample(1l, i, true, i, "key1", "url1"));
        }
        assertEquals(10, dist.getMax());
        assertEquals(1, dist.getNrBelow10Percentile());
        assertEquals(2, dist.getNrBetween10And30Percentile());
        assertEquals(4, dist.getNrBetween30And70Percentile());
        assertEquals(2, dist.getNrBetween70And90Percentile());
        assertEquals(2, dist.getNrAbove90Percentile());
    }
    
    @Test
    public void testGetDistributionLabelsArray() {
        SamplesDistribution dist = new SamplesDistribution(10);
        assertEquals("[\"\", 1, \"\", 3, \"\", 7, \"\", 9, \"\"]", dist.getDistributionLabelsArray());
    }
    
    @Test
    public void testGetDistributionsSamplesArray() {
        SamplesDistribution dist = new SamplesDistribution(10);
        for (int i = 0; i <= 10; i++) {
            dist.addSampleToDistribution(new Sample(1l, i, true, i, "key1", "url1"));
        }
        assertEquals("[1, 0, 2, 0, 4, 0, 2, 0, 2]", dist.getDistributionsSamplesArray());
    }
}
