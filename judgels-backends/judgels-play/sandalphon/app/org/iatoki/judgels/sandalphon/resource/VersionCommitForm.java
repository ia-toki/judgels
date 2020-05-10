package org.iatoki.judgels.sandalphon.resource;

import play.data.validation.Constraints;

public final class VersionCommitForm {

    @Constraints.Required
    public String title;

    public String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
