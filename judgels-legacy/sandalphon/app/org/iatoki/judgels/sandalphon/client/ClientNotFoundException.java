package org.iatoki.judgels.sandalphon.client;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ClientNotFoundException extends EntityNotFoundException {

    public ClientNotFoundException() {
        super();
    }

    public ClientNotFoundException(String s) {
        super(s);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Client";
    }
}
