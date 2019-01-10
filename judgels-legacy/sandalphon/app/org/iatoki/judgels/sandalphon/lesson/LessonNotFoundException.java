package org.iatoki.judgels.sandalphon.lesson;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class LessonNotFoundException extends EntityNotFoundException {

    public LessonNotFoundException() {
        super();
    }

    public LessonNotFoundException(String s) {
        super(s);
    }

    public LessonNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LessonNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Lesson";
    }
}
