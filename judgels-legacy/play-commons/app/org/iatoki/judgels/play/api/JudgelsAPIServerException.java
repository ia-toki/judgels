package org.iatoki.judgels.play.api;

public abstract class JudgelsAPIServerException extends RuntimeException {

    protected JudgelsAPIServerException(String message) {
        super(message);
    }

    protected JudgelsAPIServerException(Throwable cause) {
        super(cause);
    }
}
