package org.iatoki.judgels.gabriel.blackbox;

public class CompilationException extends BlackBoxGradingException {

    public CompilationException(String message) {
        super(message);
    }

    public CompilationException(Throwable cause) {
        super(cause);
    }
}
