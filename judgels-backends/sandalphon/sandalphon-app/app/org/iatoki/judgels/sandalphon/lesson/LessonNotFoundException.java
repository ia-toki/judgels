package org.iatoki.judgels.sandalphon.lesson;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class LessonNotFoundException extends EntityNotFoundException {
    public LessonNotFoundException(String s) {
        super(s);
    }

    @Override
    public String getEntityName() {
        return "Lesson";
    }
}
