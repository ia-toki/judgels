package org.iatoki.judgels.jerahmeel.curriculum.course;

import play.data.validation.Constraints;

public final class CurriculumCourseEditForm {

    @Constraints.Required
    public String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
