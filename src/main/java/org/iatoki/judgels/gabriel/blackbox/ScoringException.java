package org.iatoki.judgels.gabriel.blackbox;

public final class ScoringException extends BlackBoxGradingException {

    public ScoringException(String message) {
        super(message);
    }

    public ScoringException(Throwable cause) {
        super(cause);
    }
}
