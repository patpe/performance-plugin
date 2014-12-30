package se.raketavdelningen.ci.jenkins.performance.report;


public class ReportEntry {
    
    private int buildNr;

    private long min;
    
    private long average;
    
    private long max;
    
    private long nrOfSamples;
    
    private long nrOfFailures;
    
    private boolean success;

    public ReportEntry() {
    }
    
    public ReportEntry(int buildNr, long min, long average, long max,
            long nrOfSamples, long nrOfFailures, boolean success) {
        super();
        this.buildNr = buildNr;
        this.min = min;
        this.average = average;
        this.max = max;
        this.nrOfSamples = nrOfSamples;
        this.nrOfFailures = nrOfFailures;
        this.success = success;
    }

//    @XmlAttribute(name = "buildnr")
    public int getBuildNr() {
        return buildNr;
    }

    public void setBuildNr(int buildNr) {
        this.buildNr = buildNr;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getAverage() {
        return average;
    }

    public void setAverage(long average) {
        this.average = average;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
 
    public long getNrOfSamples() {
        return nrOfSamples;
    }

    public void setNrOfSamples(long nrOfSamples) {
        this.nrOfSamples = nrOfSamples;
    }

    public long getNrOfFailures() {
        return nrOfFailures;
    }

    public void setNrOfFailures(long nrOfFailures) {
        this.nrOfFailures = nrOfFailures;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
