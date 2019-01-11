package org.iatoki.judgels.jerahmeel.chapter.problem;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ChapterProblemNotFoundException extends EntityNotFoundException {

    public ChapterProblemNotFoundException() {
        super();
    }

    public ChapterProblemNotFoundException(String s) {
        super(s);
    }

    public ChapterProblemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChapterProblemNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Chapter Problem";
    }
}
