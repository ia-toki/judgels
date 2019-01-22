package org.iatoki.judgels.sandalphon.lesson;

import play.data.validation.Constraints;

public class LessonCreateForm {

    @Constraints.Required
    @Constraints.Pattern("^[a-z0-9]+(-[a-z0-9]+)*$")
    public String slug;

    public String additionalNote;

    @Constraints.Required
    public String initLanguageCode;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getInitLanguageCode() {
        return initLanguageCode;
    }

    public void setInitLanguageCode(String initLanguageCode) {
        this.initLanguageCode = initLanguageCode;
    }
}
