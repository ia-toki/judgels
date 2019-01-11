package org.iatoki.judgels.jerahmeel.curriculum.course;

import play.data.validation.Constraints;

public final class CurriculumCourseEditForm {

    @Constraints.Required
    public String alias;
}
