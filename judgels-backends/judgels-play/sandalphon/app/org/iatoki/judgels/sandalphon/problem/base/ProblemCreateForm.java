package org.iatoki.judgels.sandalphon.problem.base;

import play.data.validation.Constraints;

public class ProblemCreateForm {

    @Constraints.Required
    public String type;

    @Constraints.Required
    @Constraints.Pattern("^[a-z0-9]+(-[a-z0-9]+)*$")
    public String slug;

    public String additionalNote;

    @Constraints.Required
    public String initLanguageCode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
