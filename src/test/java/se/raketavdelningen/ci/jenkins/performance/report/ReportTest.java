package se.raketavdelningen.ci.jenkins.performance.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;


public class ReportTest {

    @Test
    public void testGetReportLog() {
        Report report = new Report();
        ReportLog log1 = new ReportLog("key1");
        ReportLog log2 = new ReportLog("key2");
        report.addLog(log1);
        report.addLog(log2);
        
        ReportLog log = report.getReportLog("key1");
        assertNotNull(log);
        assertEquals("key1", log.getKey());
        
        assertNull(report.getReportLog("key3"));
    }
    
    @Test
    public void testAddLog() {
        Report report = new Report();
        assertEquals(0, report.size());
        ReportLog log1 = new ReportLog("key1");
        report.addLog(log1);
        
        assertEquals(1, report.size());
        assertNotNull(report.get("key1"));
        assertEquals(log1, report.get("key1"));
    }
    
    @Test
    public void testGetReportKeys() {
        Report report = new Report();
        ReportLog log1 = new ReportLog("key1");
        ReportLog log2 = new ReportLog("key2");
        report.addLog(log1);
        report.addLog(log2);

        Set<String> keys = report.getReportKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
    }
}
