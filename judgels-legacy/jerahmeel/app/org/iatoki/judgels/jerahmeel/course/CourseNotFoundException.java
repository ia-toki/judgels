package org.iatoki.judgels.jerahmeel.course;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class CourseNotFoundException extends EntityNotFoundException {

    public CourseNotFoundException() {
        super();
    }

    public CourseNotFoundException(String s) {
        super(s);
    }

    public CourseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CourseNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Course";
    }
}
