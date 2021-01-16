package org.iatoki.judgels.play;

public abstract class EntityNotFoundException extends Exception {

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String s) {
        super(s);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public abstract String getEntityName();
}
