package org.iatoki.judgels.jerahmeel.chapter.lesson;

import play.data.validation.Constraints;

public class ChapterLessonEditForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String status;
}
