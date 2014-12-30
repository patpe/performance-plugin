package se.raketavdelningen.ci.jenkins.performance.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.ReportConstants;
import se.raketavdelningen.ci.jenkins.performance.exception.ReportException;
import se.raketavdelningen.ci.jenkins.performance.report.Report;
import se.raketavdelningen.ci.jenkins.performance.report.ReportLog;

public class ReportProjectActionTest {

    @Test
    public void testReportProjectAction() {
        Report report = new Report();
        report.addLog(new ReportLog("key1"));
        
        ReportProjectAction action = new ReportProjectAction(report);
        assertTrue(action.getReport().containsKey("key1"));
        assertNotNull(action.getReport().getReportLog("key1"));
    }
    
    @Test(expected = ReportException.class)
    public void testReportProjectActionWithNullReport() {
        new ReportProjectAction(null);
    }

    @Test
    public void testGetReport() {
        Report report = new Report();
        report.addLog(new ReportLog("key1"));
        
        ReportProjectAction action = new ReportProjectAction(report);
        assertTrue(action.getReport().containsKey("key1"));
        assertNotNull(action.getReport().getReportLog("key1"));
    }

    @Test
    public void testGetIconFileName() {
        ReportProjectAction action = new ReportProjectAction(new Report());
        assertEquals(ReportConstants.PLUGIN_ICON, action.getIconFileName());
    }

    @Test
    public void testGetDisplayName() {
        ReportProjectAction action = new ReportProjectAction(new Report());
        assertEquals(ReportConstants.PLUGIN_NAME, action.getDisplayName());
    }

    @Test
    public void testGetUrlName() {
        ReportProjectAction action = new ReportProjectAction(new Report());
        assertEquals(ReportConstants.PLUGIN_URL, action.getUrlName());
    }
}