package org.iatoki.judgels.jerahmeel.chapter.lesson;

import judgels.jerahmeel.persistence.ChapterLessonModel;

final class ChapterLessonServiceUtils {

    private ChapterLessonServiceUtils() {
        // prevent instantiation
    }

    static ChapterLesson createFromModel(ChapterLessonModel model) {
        return new ChapterLesson(model.id, model.chapterJid, model.lessonJid, model.alias, ChapterLessonStatus.valueOf(model.status));
    }
}
