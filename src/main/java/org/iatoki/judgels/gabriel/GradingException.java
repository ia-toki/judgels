package org.iatoki.judgels.gabriel;

public class GradingException extends Exception {

    public GradingException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        return getClass().getSimpleName() + ": " + message;
    }
}
