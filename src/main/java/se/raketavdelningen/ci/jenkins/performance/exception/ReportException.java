package se.raketavdelningen.ci.jenkins.performance.exception;

public class ReportException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 8066289975356704873L;

    public ReportException() {
        super();
    }

    public ReportException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable cause) {
        super(cause);
    }    
}
