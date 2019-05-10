package judgels.gabriel.api;

public class SandboxException extends RuntimeException {

    public SandboxException(String message) {
        super(message);
    }

    public SandboxException(Throwable cause) {
        super(cause);
    }
}
