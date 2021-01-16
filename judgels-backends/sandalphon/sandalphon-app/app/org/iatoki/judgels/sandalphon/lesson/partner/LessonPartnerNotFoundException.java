package org.iatoki.judgels.sandalphon.lesson.partner;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class LessonPartnerNotFoundException extends EntityNotFoundException {

    public LessonPartnerNotFoundException() {
        super();
    }

    public LessonPartnerNotFoundException(String s) {
        super(s);
    }

    public LessonPartnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LessonPartnerNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Lesson Partner";
    }
}
