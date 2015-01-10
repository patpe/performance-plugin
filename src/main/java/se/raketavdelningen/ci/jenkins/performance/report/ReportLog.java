package se.raketavdelningen.ci.jenkins.performance.report;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportLog extends ArrayList<ReportEntry> {

    /**
     * 
     */
    private static final long serialVersionUID = -1101804755620529958L;

    private String key;

    public ReportLog(String key) {
        super();
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public void addEntry(ReportEntry entry) {
        add(entry);
    }

    /**
     * Supports Chart.js
     *  
     * @return ["#1", "#2", "#3"]
     */
    public String getBuildNrArray() {
        String[] result = new String[size()];
        int index = 0;
        for (ReportEntry entry : this) {
            result[index] = "\"#" + entry.getBuildNr() + "\"";
            index++;
        }
        return Arrays.toString(result);
    }

    /**
     * Supports Chart.js
     *  
     * @return [1, 2, 3]
     */
    public String getMinArray() {
        long[] result = new long[size()];
        int index = 0;
        for (ReportEntry entry : this) {
            result[index] = entry.getMin();
            index++;
        }
        return Arrays.toString(result);        
    }

    /**
     * Supports Chart.js
     *  
     * @return [1, 2, 3]
     */
    public String getAverageArray() {
        long[] result = new long[size()];
        int index = 0;
        for (ReportEntry entry : this) {
            result[index] = entry.getAverage();
            index++;
        }
        return Arrays.toString(result);        
    }
    
    /**
     * Supports Chart.js
     *  
     * @return [1, 2, 3]
     */
    public String getMaxArray() {
        long[] result = new long[size()];
        int index = 0;
        for (ReportEntry entry : this) {
            result[index] = entry.getMax();
            index++;
        }
        return Arrays.toString(result);        
    }
    
    /**
     * Supports Chart.js
     *  
     * @return [1, 2, 3]
     */
    public String getPercentile95Array() {
        long[] result = new long[size()];
        int index = 0;
        for (ReportEntry entry : this) {
            result[index] = entry.getPercentile95();
            index++;
        }
        return Arrays.toString(result);
    }
}
