package org.iatoki.judgels.jerahmeel.course.chapter;

import play.data.validation.Constraints;

public final class CourseChapterEditForm {

    @Constraints.Required
    public String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
