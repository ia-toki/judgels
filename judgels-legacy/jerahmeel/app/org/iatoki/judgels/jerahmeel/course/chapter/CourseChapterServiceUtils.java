package org.iatoki.judgels.jerahmeel.course.chapter;

import judgels.jerahmeel.persistence.CourseChapterModel;

final class CourseChapterServiceUtils {

    private CourseChapterServiceUtils() {
        // prevent instantiation
    }

    static CourseChapter createFromModel(CourseChapterModel model) {
        return new CourseChapter(model.id, model.courseJid, model.chapterJid, model.alias);
    }
}
