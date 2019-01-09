package judgels.gabriel.api;

public abstract class GradingException extends Exception {

    public GradingException(String message) {
        super(message);
    }

    public GradingException(Throwable cause) {
        super(cause);
    }
}
