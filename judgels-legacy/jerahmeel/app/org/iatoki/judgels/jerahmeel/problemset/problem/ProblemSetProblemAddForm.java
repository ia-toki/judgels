package org.iatoki.judgels.jerahmeel.problemset.problem;

import play.data.validation.Constraints;

public final class ProblemSetProblemAddForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String problemJid;

    @Constraints.Required
    public String problemSecret;

    @Constraints.Required
    public String type;

    @Constraints.Required
    public String status;
}
