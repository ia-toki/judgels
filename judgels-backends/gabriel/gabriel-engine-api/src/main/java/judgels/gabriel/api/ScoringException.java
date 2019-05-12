package judgels.gabriel.api;

public class ScoringException extends EvaluationException {

    public ScoringException(String message) {
        super(message);
    }

    public ScoringException(Throwable cause) {
        super(cause);
    }
}
