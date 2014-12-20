package se.raketavdelningen.ci.jenkins.performance.sample;

public class PerformanceSample {

    private long timestamp;
    
    private long elapsed;
    
    private boolean success;
    
    private long bytes;
    
    private String label;
    
    private String url;

    public PerformanceSample(long timestamp, long elapsed,
            boolean success, long bytes, String label, String url) {
        super();
        this.timestamp = timestamp;
        this.elapsed = elapsed;
        this.success = success;
        this.bytes = bytes;
        this.label = label;
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PerformanceSample [timestamp=" + timestamp + ", elapsed="
                + elapsed + ", success=" + success + ", bytes=" + bytes
                + ", label=" + label + ", url=" + url + "]";
    }
}
