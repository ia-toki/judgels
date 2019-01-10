package org.iatoki.judgels.sandalphon.lesson.partner;

import play.data.validation.Constraints;

public final class LessonPartnerUsernameForm {

    @Constraints.Required
    public String username;
}
