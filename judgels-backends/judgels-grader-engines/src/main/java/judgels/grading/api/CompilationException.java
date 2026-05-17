package judgels.grading.api;

public class CompilationException extends GradingException {

    public CompilationException(String message) {
        super(message);
    }

    public CompilationException(Throwable cause) {
        super(cause);
    }
}
