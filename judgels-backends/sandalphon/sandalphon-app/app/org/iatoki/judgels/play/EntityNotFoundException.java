package org.iatoki.judgels.play;

public abstract class EntityNotFoundException extends Exception {

    public EntityNotFoundException() {}

    public EntityNotFoundException(String s) {
        super(s);
    }

    public abstract String getEntityName();
}
