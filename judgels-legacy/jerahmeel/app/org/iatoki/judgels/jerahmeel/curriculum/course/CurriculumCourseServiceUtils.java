package org.iatoki.judgels.jerahmeel.curriculum.course;

final class CurriculumCourseServiceUtils {

    private CurriculumCourseServiceUtils() {
        // prevent instantiation
    }

    static CurriculumCourse createFromModel(CurriculumCourseModel model) {
        return new CurriculumCourse(model.id, model.curriculumJid, model.courseJid, model.alias);
    }
}
