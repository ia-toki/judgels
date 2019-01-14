package org.iatoki.judgels.gabriel;

public class ResponseException extends GabrielWorkerException {

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

}
