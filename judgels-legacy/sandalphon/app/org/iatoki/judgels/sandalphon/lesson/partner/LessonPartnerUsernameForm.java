package org.iatoki.judgels.sandalphon.lesson.partner;

import play.data.validation.Constraints;

public final class LessonPartnerUsernameForm {

    @Constraints.Required
    public String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
