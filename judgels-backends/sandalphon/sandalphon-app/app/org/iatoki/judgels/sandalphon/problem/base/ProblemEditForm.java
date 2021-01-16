package org.iatoki.judgels.sandalphon.problem.base;

import play.data.validation.Constraints;

public final class ProblemEditForm {

    @Constraints.Required
    @Constraints.Pattern("^[a-z0-9]+(-[a-z0-9]+)*$")
    public String slug;

    public String additionalNote;

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
}
