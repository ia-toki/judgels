package org.iatoki.judgels.jerahmeel.course.chapter;

import play.data.validation.Constraints;

public final class CourseChapterEditForm {

    @Constraints.Required
    public String alias;
}
