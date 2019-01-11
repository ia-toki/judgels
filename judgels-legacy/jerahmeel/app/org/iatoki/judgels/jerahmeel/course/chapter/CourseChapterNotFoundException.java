package org.iatoki.judgels.jerahmeel.course.chapter;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class CourseChapterNotFoundException extends EntityNotFoundException {

    public CourseChapterNotFoundException() {
        super();
    }

    public CourseChapterNotFoundException(String s) {
        super(s);
    }

    public CourseChapterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CourseChapterNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Course Chapter";
    }
}
