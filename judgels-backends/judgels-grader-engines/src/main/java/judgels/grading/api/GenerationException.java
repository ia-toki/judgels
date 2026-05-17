package judgels.grading.api;

public class GenerationException extends EvaluationException {

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(Throwable cause) {
        super(cause);
    }
}
