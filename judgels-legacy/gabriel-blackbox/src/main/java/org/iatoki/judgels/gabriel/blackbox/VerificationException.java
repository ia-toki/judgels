package org.iatoki.judgels.gabriel.blackbox;

public final class VerificationException extends BlackBoxGradingException {

    public VerificationException(String message) {
        super(message);
    }

    public VerificationException(Throwable cause) {
        super(cause);
    }
}
