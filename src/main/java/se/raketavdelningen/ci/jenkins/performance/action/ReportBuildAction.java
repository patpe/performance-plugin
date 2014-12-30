package se.raketavdelningen.ci.jenkins.performance.action;

import hudson.model.Action;
import hudson.model.AbstractProject;
import se.raketavdelningen.ci.jenkins.performance.ReportConstants;
import se.raketavdelningen.ci.jenkins.performance.report.Report;
import se.raketavdelningen.ci.jenkins.performance.sample.SamplesMap;

public class ReportBuildAction implements Action {

    private SamplesMap samples = null;
    
    private Report report = null;

    private AbstractProject<?, ?> project;
    
    public ReportBuildAction(SamplesMap samples, Report report, AbstractProject<?, ?> project) {
        super();
        this.samples = samples;
        this.report = report;
        this.project = project;
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

    public AbstractProject<?, ?> getProject() {
        return project;
    }

    public void setProject(AbstractProject<?, ?> project) {
        this.project = project;
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