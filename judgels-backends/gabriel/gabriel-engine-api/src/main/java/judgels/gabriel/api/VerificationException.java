package judgels.gabriel.api;

public class VerificationException extends GradingException {

    public VerificationException(String message) {
        super(message);
    }

    public VerificationException(Throwable cause) {
        super(cause);
    }
}
