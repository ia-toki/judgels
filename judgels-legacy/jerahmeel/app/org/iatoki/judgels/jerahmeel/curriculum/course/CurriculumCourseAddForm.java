package org.iatoki.judgels.jerahmeel.curriculum.course;

import play.data.validation.Constraints;

public final class CurriculumCourseAddForm {

    @Constraints.Required
    public String courseJid;

    @Constraints.Required
    public String alias;
}
