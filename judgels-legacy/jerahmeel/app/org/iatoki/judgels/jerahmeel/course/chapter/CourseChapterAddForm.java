package org.iatoki.judgels.jerahmeel.course.chapter;

import play.data.validation.Constraints;

public final class CourseChapterAddForm {

    @Constraints.Required
    public String chapterJid;

    @Constraints.Required
    public String alias;

    public String getChapterJid() {
        return chapterJid;
    }

    public void setChapterJid(String chapterJid) {
        this.chapterJid = chapterJid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
