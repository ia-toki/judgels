package judgels.gabriel.api;

public class ScoringException extends GradingException {

    public ScoringException(String message) {
        super(message);
    }

    public ScoringException(Throwable cause) {
        super(cause);
    }
}
