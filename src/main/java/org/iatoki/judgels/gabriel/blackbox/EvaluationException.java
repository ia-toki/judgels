package org.iatoki.judgels.gabriel.blackbox;

public final class EvaluationException extends BlackBoxGradingException {

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(Throwable cause) {
        super(cause);
    }
}
