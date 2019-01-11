package org.iatoki.judgels.jerahmeel.chapter;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ChapterNotFoundException extends EntityNotFoundException {

    public ChapterNotFoundException() {
        super();
    }

    public ChapterNotFoundException(String s) {
        super(s);
    }

    public ChapterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChapterNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Chapter";
    }
}
