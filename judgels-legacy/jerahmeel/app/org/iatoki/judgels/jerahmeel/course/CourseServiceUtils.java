package org.iatoki.judgels.jerahmeel.course;

import judgels.jerahmeel.persistence.CourseModel;

final class CourseServiceUtils {

    private CourseServiceUtils() {
        // prevent instantiation
    }

    static Course createCourseFromModel(CourseModel courseModel) {
        return new Course(courseModel.id, courseModel.jid, courseModel.slug, courseModel.name, courseModel.description);
    }
}
