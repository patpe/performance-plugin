package se.raketavdelningen.ci.jenkins.performance.action;

import hudson.model.Action;
import se.raketavdelningen.ci.jenkins.performance.ReportConstants;
import se.raketavdelningen.ci.jenkins.performance.exception.ReportException;
import se.raketavdelningen.ci.jenkins.performance.report.Report;

public class ReportProjectAction implements Action {
    
    private Report report;
    
    public ReportProjectAction(Report report) {
        super();
        if (report == null) {
            throw new ReportException("Report can't be null");
        }
        this.report = report;
    }
    
    public Report getReport() {
        return report;
    }

    @Override
    public String getIconFileName() {
        return ReportConstants.PLUGIN_ICON;
    }

    @Override
    public String getDisplayName() {
        return ReportConstants.PLUGIN_NAME;
    }

    @Override
    public String getUrlName() {
        return ReportConstants.PLUGIN_URL;
    }
}
