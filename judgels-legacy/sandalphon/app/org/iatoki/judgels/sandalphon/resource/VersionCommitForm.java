package org.iatoki.judgels.sandalphon.resource;

import play.data.validation.Constraints;

public final class VersionCommitForm {

    @Constraints.Required
    public String title;

    public String description;
}
