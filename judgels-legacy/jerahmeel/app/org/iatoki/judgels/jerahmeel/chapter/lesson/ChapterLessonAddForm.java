package org.iatoki.judgels.jerahmeel.chapter.lesson;

import play.data.validation.Constraints;

public final class ChapterLessonAddForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String lessonSlug;

    @Constraints.Required
    public String status;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLessonSlug() {
        return lessonSlug;
    }

    public void setLessonSlug(String lessonSlug) {
        this.lessonSlug = lessonSlug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
