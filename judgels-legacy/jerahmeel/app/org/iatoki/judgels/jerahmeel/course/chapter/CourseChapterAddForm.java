package org.iatoki.judgels.jerahmeel.course.chapter;

import play.data.validation.Constraints;

public final class CourseChapterAddForm {

    @Constraints.Required
    public String chapterJid;

    @Constraints.Required
    public String alias;
}
