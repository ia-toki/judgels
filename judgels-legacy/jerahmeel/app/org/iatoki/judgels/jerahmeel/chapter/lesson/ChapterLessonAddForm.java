package org.iatoki.judgels.jerahmeel.chapter.lesson;

import play.data.validation.Constraints;

public final class ChapterLessonAddForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String lessonSlug;

    @Constraints.Required
    public String status;
}
