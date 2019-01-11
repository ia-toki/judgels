package org.iatoki.judgels.jerahmeel.curriculum.course;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class CurriculumCourseNotFoundException extends EntityNotFoundException {

    public CurriculumCourseNotFoundException() {
        super();
    }

    public CurriculumCourseNotFoundException(String s) {
        super(s);
    }

    public CurriculumCourseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurriculumCourseNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Curriculum Course";
    }
}
