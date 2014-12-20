package se.raketavdelningen.ci.jenkins.performance.exception;

public class PerformanceReportException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -3899478315339744116L;

    public PerformanceReportException() {
        super();
    }

    public PerformanceReportException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PerformanceReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public PerformanceReportException(String message) {
        super(message);
    }

    public PerformanceReportException(Throwable cause) {
        super(cause);
    }
}
