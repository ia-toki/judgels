package org.iatoki.judgels.jerahmeel.chapter.lesson;

final class ChapterLessonServiceUtils {

    private ChapterLessonServiceUtils() {
        // prevent instantiation
    }

    static ChapterLesson createFromModel(ChapterLessonModel model) {
        return new ChapterLesson(model.id, model.chapterJid, model.lessonJid, model.lessonSecret, model.alias, ChapterLessonStatus.valueOf(model.status));
    }
}
