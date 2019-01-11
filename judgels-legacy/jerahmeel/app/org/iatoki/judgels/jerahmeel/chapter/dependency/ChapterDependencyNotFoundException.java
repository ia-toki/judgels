package org.iatoki.judgels.jerahmeel.chapter.dependency;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ChapterDependencyNotFoundException extends EntityNotFoundException {

    public ChapterDependencyNotFoundException() {
        super();
    }

    public ChapterDependencyNotFoundException(String s) {
        super(s);
    }

    public ChapterDependencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChapterDependencyNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Chapter Dependency";
    }
}
