package judgels.grading.api;

public class ScoringException extends EvaluationException {

    public ScoringException(String message) {
        super(message);
    }

    public ScoringException(Throwable cause) {
        super(cause);
    }
}
