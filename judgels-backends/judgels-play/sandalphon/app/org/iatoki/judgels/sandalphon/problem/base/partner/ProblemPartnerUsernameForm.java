package org.iatoki.judgels.sandalphon.problem.base.partner;

import play.data.validation.Constraints;

public final class ProblemPartnerUsernameForm {

    @Constraints.Required
    public String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
