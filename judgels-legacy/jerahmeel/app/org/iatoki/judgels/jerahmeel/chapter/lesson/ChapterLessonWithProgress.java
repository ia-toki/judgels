package org.iatoki.judgels.jerahmeel.chapter.lesson;

public final class ChapterLessonWithProgress {

    private final ChapterLesson chapterLesson;
    private final LessonProgress lessonProgress;

    public ChapterLessonWithProgress(ChapterLesson chapterLesson, LessonProgress lessonProgress) {
        this.chapterLesson = chapterLesson;
        this.lessonProgress = lessonProgress;
    }

    public ChapterLesson getChapterLesson() {
        return chapterLesson;
    }

    public LessonProgress getLessonProgress() {
        return lessonProgress;
    }
}
