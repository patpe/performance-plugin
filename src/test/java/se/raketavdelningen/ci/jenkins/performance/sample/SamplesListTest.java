package se.raketavdelningen.ci.jenkins.performance.sample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SamplesListTest {

    /**
     * This is 3.00PM, 12/30 2014 
     */
    private static final Long DEFAULT_TIMESTAMP = new Long(1419948054990l);

    @Test
    public void testGetTimestampArray() {
        SamplesList list = new SamplesList();
        assertEquals("[]", list.getTimestampArray());
    }
    
    @Test
    public void testGetTimestampArrayWithOneElement() {
        SamplesList list = new SamplesList();
        AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, 1, 10, true, 1000, 0, "key1");
        list.add(sample);
        assertEquals("[\"15:00\"]", list.getTimestampArray());
    }

    @Test
    public void testGetTimestampArrayWithTwoElements() {
        SamplesList list = new SamplesList();
        AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, 1, 10, true, 1000, 0, "key1");
        list.add(sample);
        sample = new AggregatedSample(DEFAULT_TIMESTAMP + 60*1000, 20, 1, 10, true, 1000, 0, "key1");
        list.add(sample);
        assertEquals("[\"15:00\", \"15:01\"]", list.getTimestampArray());
    }

    @Test
    public void testGetTimestampArrayEqualAmountOfElements() {
        SamplesList list = new SamplesList();
        for (int i = 0; i < 6; i++) {
            AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP + (i*60*1000), 20, 1, 10, true, 1000, 0, "key1");
            list.add(sample);
        }
        assertEquals("[\"15:00\", \"\", \"15:02\", \"\", \"\", \"15:05\"]", list.getTimestampArray());
    }

    @Test
    public void testGetTimestampArrayOddAmountOfElements() {
        SamplesList list = new SamplesList();
        for (int i = 0; i < 5; i++) {
            AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP + (i*60*1000), 20, 1, 10, true, 1000, 0, "key1");
            list.add(sample);
        }
        assertEquals("[\"15:00\", \"\", \"15:02\", \"\", \"15:04\"]", list.getTimestampArray());
    }
    
    @Test
    public void testGetMinArray() {
        SamplesList list = new SamplesList();
        for (int i = 0; i < 6; i++) {
            AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, i, 10, true, 1000, 0, "key1");
            list.add(sample);
        }
        assertEquals("[0, 1, 2, 3, 4, 5]", list.getMinArray());
    }

    @Test
    public void testGetMinArrayFromListWithOneElement() {
        SamplesList list = new SamplesList();
        AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, 5, 10, true, 1000, 0, "key1");
        list.add(sample);
        assertEquals("[5]", list.getMinArray());
    }
    
    @Test
    public void testGetMinArrayFromEmptyList() {
        SamplesList list = new SamplesList();
        assertEquals("[]", list.getMinArray());
    }

    @Test
    public void testGetAverageArray() {
        SamplesList list = new SamplesList();
        for (int i = 0; i < 6; i++) {
            AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, 1, i, true, 1000, 0, "key1");
            list.add(sample);
        }
        assertEquals("[0, 1, 2, 3, 4, 5]", list.getAverageArray());
    }

    @Test
    public void testGetAverageArrayFromEmptyList() {
        SamplesList list = new SamplesList();
        assertEquals("[]", list.getAverageArray());
    }

    @Test
    public void testGetAverageArrayListWithOneElement() {
        SamplesList list = new SamplesList();
        AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, 1, 10, true, 1000, 0, "key1");
        list.add(sample);
        assertEquals("[10]", list.getAverageArray());
    }
    
    @Test
    public void testGetMaxArray() {
        SamplesList list = new SamplesList();
        for (int i = 0; i < 6; i++) {
            AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, i, 10, 20, true, 1000, 0, "key1");
            list.add(sample);
        }
        assertEquals("[0, 1, 2, 3, 4, 5]", list.getMaxArray());
    }
    
    @Test
    public void testGetMaxArrayFromEmptyList() {
        SamplesList list = new SamplesList();
        assertEquals("[]", list.getMaxArray());
    }

    @Test
    public void testGetMaxArrayListWithOneElement() {
        SamplesList list = new SamplesList();
        AggregatedSample sample = new AggregatedSample(DEFAULT_TIMESTAMP, 20, 1, 10, true, 1000, 0, "key1");
        list.add(sample);
        assertEquals("[20]", list.getMaxArray());
    }
}
