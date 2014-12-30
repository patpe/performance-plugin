package se.raketavdelningen.ci.jenkins.performance.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.ReportConstants;
import se.raketavdelningen.ci.jenkins.performance.report.Report;
import se.raketavdelningen.ci.jenkins.performance.report.ReportLog;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedSample;
import se.raketavdelningen.ci.jenkins.performance.sample.SamplesList;
import se.raketavdelningen.ci.jenkins.performance.sample.SamplesMap;

public class ReportBuildActionTest {

    @Test
    public void testReportBuildAction() {
        SamplesMap samples = new SamplesMap();
        samples.put("key1", new SamplesList());
        
        Report report = new Report();
        report.addLog(new ReportLog("key1"));
        
        ReportBuildAction action = new ReportBuildAction(samples, report);
        assertEquals(report, action.getReport());
        assertEquals(samples, action.getSamples());
    }

    @Test
    public void testGetSamples() {
        SamplesList list = new SamplesList();
        for (int i = 0; i < 6; i++) {
            AggregatedSample sample = new AggregatedSample(System.currentTimeMillis() + (i*60*1000), 20, 1, 10, true, 1000, 0, "key1");
            list.add(sample);
        }
        
        SamplesMap samples = new SamplesMap();
        samples.put("key1", list);

        ReportBuildAction action = new ReportBuildAction(samples, null);
        SamplesMap map = action.getSamples();
        assertNotNull(map);
        assertEquals(1, map.size());
        assertNotNull(map.get("key1"));
        assertEquals(6, map.get("key1").size());
    }

    @Test
    public void testGetReport() {
        Report report = new Report();
        report.addLog(new ReportLog("key1"));
        
        ReportBuildAction action = new ReportBuildAction(null,  report);
        
        Report r = action.getReport();
        assertNotNull(r);
        assertEquals(1, r.size());
        assertTrue(action.getReport().containsKey("key1"));
    }

    @Test
    public void testGetIconFileName() {
        ReportBuildAction action = new ReportBuildAction(null,  null);
        assertEquals(ReportConstants.PLUGIN_ICON, action.getIconFileName());
    }

    @Test
    public void testGetDisplayName() {
        ReportBuildAction action = new ReportBuildAction(null,  null);
        assertEquals(ReportConstants.PLUGIN_NAME, action.getDisplayName());
    }

    @Test
    public void testGetUrlName() {
        ReportBuildAction action = new ReportBuildAction(null,  null);
        assertEquals(ReportConstants.PLUGIN_URL, action.getUrlName());
    }

}
