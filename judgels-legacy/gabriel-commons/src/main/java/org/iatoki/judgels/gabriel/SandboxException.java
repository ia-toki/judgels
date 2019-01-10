package org.iatoki.judgels.gabriel;

public final class SandboxException extends RuntimeException {
    public SandboxException(String message) {
        super(message);
    }

    public SandboxException(Exception e) {
        super(e);
    }
}
