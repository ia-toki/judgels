package org.iatoki.judgels.jerahmeel.chapter.lesson;

public final class ChapterLesson {

    private final long id;
    private final String chapterJid;
    private final String lessonJid;
    private final String lessonSecret;
    private final String alias;
    private final ChapterLessonStatus status;

    public ChapterLesson(long id, String chapterJid, String lessonJid, String lessonSecret, String alias, ChapterLessonStatus status) {
        this.id = id;
        this.chapterJid = chapterJid;
        this.lessonJid = lessonJid;
        this.lessonSecret = lessonSecret;
        this.alias = alias;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getChapterJid() {
        return chapterJid;
    }

    public String getLessonJid() {
        return lessonJid;
    }

    public String getLessonSecret() {
        return lessonSecret;
    }

    public String getAlias() {
        return alias;
    }

    public ChapterLessonStatus getStatus() {
        return status;
    }
}
