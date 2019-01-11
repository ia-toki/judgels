package org.iatoki.judgels.jerahmeel.curriculum.course;

public final class CurriculumCourse {

    private final long id;
    private final String curriculumJid;
    private final String courseJid;
    private final String alias;

    public CurriculumCourse(long id, String curriculumJid, String courseJid, String alias) {
        this.id = id;
        this.curriculumJid = curriculumJid;
        this.courseJid = courseJid;
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public String getCurriculumJid() {
        return curriculumJid;
    }

    public String getCourseJid() {
        return courseJid;
    }

    public String getAlias() {
        return alias;
    }
}
