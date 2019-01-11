package org.iatoki.judgels.jerahmeel.course;

final class CourseServiceUtils {

    private CourseServiceUtils() {
        // prevent instantiation
    }

    static Course createCourseFromModel(CourseModel courseModel) {
        return new Course(courseModel.id, courseModel.jid, courseModel.name, courseModel.description);
    }
}
