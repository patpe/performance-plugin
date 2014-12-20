package se.raketavdelningen.ci.jenkins.performance;

import hudson.model.Action;

public class PerformanceReportBuildAction implements Action {

    public String getIconFileName() {
        return PerformanceReportConstants.PLUGIN_ICON;
    }

    public String getDisplayName() {
        return PerformanceReportConstants.PLUGIN_NAME;
    }

    public String getUrlName() {
        return PerformanceReportConstants.PLUGIN_URL;
    }
}
