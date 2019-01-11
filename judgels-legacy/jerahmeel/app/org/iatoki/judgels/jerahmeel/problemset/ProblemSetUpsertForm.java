package org.iatoki.judgels.jerahmeel.problemset;

import play.data.validation.Constraints;

public final class ProblemSetUpsertForm {

    @Constraints.Required
    public String archiveJid;

    @Constraints.Required
    public String name;

    public String description;
}
