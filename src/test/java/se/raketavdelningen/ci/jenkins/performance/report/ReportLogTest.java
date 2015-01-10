package se.raketavdelningen.ci.jenkins.performance.report;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReportLogTest {

    @Test
    public void testGetKey() {
        ReportLog log = new ReportLog("key");
        assertEquals("key", log.getKey());
    }

    @Test
    public void testSetKey() {
        ReportLog log = new ReportLog("key");
        assertEquals("key", log.getKey());

        log.setKey("key1");
        assertEquals("key1", log.getKey());
    }

    @Test
    public void testAddEntry() {
        ReportLog log = new ReportLog("key1");
        assertEquals(0, log.size());

        int buildNr = 1;
        long min = 2;
        long average = 3;
        long max = 4;
        long percentile95 = 3;
        long nrOfSamples = 5;
        long nrOfFailures = 6;
        boolean success = true;
        ReportEntry entry = new ReportEntry(buildNr, min, average, max, percentile95, nrOfSamples, nrOfFailures, success);
        log.addEntry(entry);

        assertEquals("key1", log.getKey());
        assertEquals(1, log.size());
        assertEquals(buildNr, log.get(0).getBuildNr());
        assertEquals(min, log.get(0).getMin());
    }

    @Test
    public void testGetBuildNrArray() {
        ReportLog log = new ReportLog("key1");

        long min = 2;
        long average = 3;
        long max = 4;
        long percentile95 = 3;
        long nrOfSamples = 5;
        long nrOfFailures = 6;
        boolean success = true;
        for (int buildNr = 1; buildNr <= 5; buildNr++) {
            ReportEntry entry = new ReportEntry(buildNr, min, average, max, percentile95, nrOfSamples, nrOfFailures, success);
            log.addEntry(entry);
        }        
        assertEquals(5, log.size());
        assertEquals("[\"#1\", \"#2\", \"#3\", \"#4\", \"#5\"]", log.getBuildNrArray());
    }

    @Test
    public void testGetMinArray() {
        ReportLog log = new ReportLog("key1");

        int buildNr = 1;
        long average = 3;
        long max = 4;
        long percentile95 = 3;
        long nrOfSamples = 5;
        long nrOfFailures = 6;
        boolean success = true;
        for (long min = 10; min <= 15; min++) {
            ReportEntry entry = new ReportEntry(buildNr, min, average, max, percentile95, nrOfSamples, nrOfFailures, success);
            log.addEntry(entry);
        }        
        assertEquals(6, log.size());
        assertEquals("[10, 11, 12, 13, 14, 15]", log.getMinArray());
    }

    @Test
    public void testGetAverageArray() {
        ReportLog log = new ReportLog("key1");

        int buildNr = 1;
        long min = 2;
        long max = 4;
        long percentile95 = 3;
        long nrOfSamples = 5;
        long nrOfFailures = 6;
        boolean success = true;
        for (long average = 20; average <= 25; average++) {
            ReportEntry entry = new ReportEntry(buildNr, min, average, max, percentile95, nrOfSamples, nrOfFailures, success);
            log.addEntry(entry);
        }        
        assertEquals(6, log.size());
        assertEquals("[20, 21, 22, 23, 24, 25]", log.getAverageArray());
    }

    @Test
    public void testGetMaxArray() {
        ReportLog log = new ReportLog("key1");

        int buildNr = 1;
        long min = 2;
        long average = 3;
        long percentile95 = 3;
        long nrOfSamples = 5;
        long nrOfFailures = 6;
        boolean success = true;
        for (long max = 30; max <= 35; max++) {
            ReportEntry entry = new ReportEntry(buildNr, min, average, max, percentile95, nrOfSamples, nrOfFailures, success);
            log.addEntry(entry);
        }        
        assertEquals(6, log.size());
        assertEquals("[30, 31, 32, 33, 34, 35]", log.getMaxArray());
    }

    @Test
    public void testGetPercentile95Array() {
        ReportLog log = new ReportLog("key1");

        int buildNr = 1;
        long min = 2;
        long average = 3;
        long max = 4;
        long nrOfSamples = 5;
        long nrOfFailures = 6;
        boolean success = true;
        for (long percentile95 = 40; percentile95 <= 45; percentile95++) {
            ReportEntry entry = new ReportEntry(buildNr, min, average, max, percentile95, nrOfSamples, nrOfFailures, success);
            log.addEntry(entry);
        }        
        assertEquals(6, log.size());
        assertEquals("[40, 41, 42, 43, 44, 45]", log.getPercentile95Array());
    }
}
