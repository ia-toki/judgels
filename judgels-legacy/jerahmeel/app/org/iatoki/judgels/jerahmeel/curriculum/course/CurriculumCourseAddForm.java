package org.iatoki.judgels.jerahmeel.curriculum.course;

import play.data.validation.Constraints;

public final class CurriculumCourseAddForm {

    @Constraints.Required
    public String courseJid;

    @Constraints.Required
    public String alias;

    public String getCourseJid() {
        return courseJid;
    }

    public void setCourseJid(String courseJid) {
        this.courseJid = courseJid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
