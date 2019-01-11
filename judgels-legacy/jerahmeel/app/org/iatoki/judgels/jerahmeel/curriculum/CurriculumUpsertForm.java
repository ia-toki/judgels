package org.iatoki.judgels.jerahmeel.curriculum;

import play.data.validation.Constraints;

public final class CurriculumUpsertForm {

    @Constraints.Required
    public String name;

    public String description;
}
