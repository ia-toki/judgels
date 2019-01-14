package org.iatoki.judgels.gabriel;

public abstract class GabrielWorkerException extends Exception {

    public GabrielWorkerException(String message) {
        super(message);
    }

    public GabrielWorkerException(Throwable cause) {
        super(cause);
    }
}
