package se.raketavdelningen.ci.jenkins.performance.action;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.util.List;
import java.util.Map;
import java.util.Set;

import se.raketavdelningen.ci.jenkins.performance.ReportConstants;
import se.raketavdelningen.ci.jenkins.performance.report.Report;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedSample;
import se.raketavdelningen.ci.jenkins.performance.sample.SamplesMap;

public class ReportBuildAction implements Action {

    private final AbstractBuild<?, ?> build;

    private SamplesMap samples = null;
    
    private Report report = null;

    private Set<String> reportKeys = null;

    public ReportBuildAction(SamplesMap samples, Report report, Set<String> reportKeys, AbstractBuild<?, ?> build) {
        super();
        this.samples = samples;
        this.report = report;
        this.reportKeys = reportKeys;
        this.build = build;
    }

    public SamplesMap getSamples() {
        return samples;
    }

    public void setSamples(SamplesMap samples) {
        this.samples = samples;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Set<String> getReportKeys() {
        return reportKeys;
    }

    public void setReportKeys(Set<String> reportKeys) {
        this.reportKeys = reportKeys;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public String getIconFileName() {
        return ReportConstants.PLUGIN_ICON;
    }

    public String getDisplayName() {
        return ReportConstants.PLUGIN_NAME;
    }

    public String getUrlName() {
        return ReportConstants.PLUGIN_URL;
    }
}