package org.iatoki.judgels.jophiel.profile;

import play.data.validation.Constraints;

public final class SearchProfileForm {

    @Constraints.Required
    public String username;
}
