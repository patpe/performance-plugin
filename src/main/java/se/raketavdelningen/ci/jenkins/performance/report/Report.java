package se.raketavdelningen.ci.jenkins.performance.report;

import java.util.HashMap;
import java.util.Set;

public class Report extends HashMap<String, ReportLog> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8280839808371485548L;

    public Report() {
    }
    
    public Set<String> getReportKeys() {
        return this.keySet();
    }
    
    public ReportLog getReportLog(String key) {
        return get(key);
    }
    
    public void addLog(ReportLog log) {
        put(log.getKey(), log);
    }
}
