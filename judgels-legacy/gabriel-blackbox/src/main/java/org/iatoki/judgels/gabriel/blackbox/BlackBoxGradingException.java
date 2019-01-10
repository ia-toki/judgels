package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingException;

public abstract class BlackBoxGradingException extends GradingException {

    protected BlackBoxGradingException(String message) {
        super(message);
    }

    protected BlackBoxGradingException(Throwable cause) {
        super(cause);
    }
}
