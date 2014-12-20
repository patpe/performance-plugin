package se.raketavdelningen.ci.jenkins.performance.sample;

public class AggregatedPerformanceSample {

    private long timestamp;
    
    private long max;
    
    private long min;
    
    private long average;
    
    private boolean success;
    
    private long nrOfSamples;
    
    private String key;

    public AggregatedPerformanceSample(long timestamp, long max, long min,
            long average, boolean success, long nrOfSamples, String key) {
        super();
        this.timestamp = timestamp;
        this.max = max;
        this.min = min;
        this.average = average;
        this.success = success;
        this.nrOfSamples = nrOfSamples;
        this.key = key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getNrOfSamples() {
        return nrOfSamples;
    }

    public void setNrOfSamples(long nrOfSamples) {
        this.nrOfSamples = nrOfSamples;
    }

    public String getSampleToken() {
        return key;
    }

    public void setSampleToken(String sampleToken) {
        this.key = sampleToken;
    }
}
