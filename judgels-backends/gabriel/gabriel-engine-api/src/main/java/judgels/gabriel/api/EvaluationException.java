package judgels.gabriel.api;

public class EvaluationException extends GradingException {

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(Throwable cause) {
        super(cause);
    }
}
