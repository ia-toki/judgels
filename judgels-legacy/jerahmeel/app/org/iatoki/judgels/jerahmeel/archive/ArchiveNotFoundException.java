package org.iatoki.judgels.jerahmeel.archive;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ArchiveNotFoundException extends EntityNotFoundException {

    public ArchiveNotFoundException() {
        super();
    }

    public ArchiveNotFoundException(String s) {
        super(s);
    }

    public ArchiveNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Archive";
    }
}
