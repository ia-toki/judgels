package org.iatoki.judgels.jerahmeel.course.chapter;

public final class CourseChapter {

    private final long id;
    private final String courseJid;
    private final String chapterJid;
    private final String alias;

    public CourseChapter(long id, String courseJid, String chapterJid, String alias) {
        this.id = id;
        this.courseJid = courseJid;
        this.chapterJid = chapterJid;
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public String getCourseJid() {
        return courseJid;
    }

    public String getChapterJid() {
        return chapterJid;
    }

    public String getAlias() {
        return alias;
    }
}
