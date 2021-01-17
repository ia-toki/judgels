package org.iatoki.judgels.sandalphon.lesson.partner;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class LessonPartnerNotFoundException extends EntityNotFoundException {
    public LessonPartnerNotFoundException(String s) {
        super(s);
    }

    @Override
    public String getEntityName() {
        return "Lesson Partner";
    }
}
