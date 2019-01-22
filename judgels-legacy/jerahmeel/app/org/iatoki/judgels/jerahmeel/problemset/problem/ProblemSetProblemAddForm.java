package org.iatoki.judgels.jerahmeel.problemset.problem;

import play.data.validation.Constraints;

public final class ProblemSetProblemAddForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String problemSlug;

    @Constraints.Required
    public String type;

    @Constraints.Required
    public String status;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getProblemSlug() {
        return problemSlug;
    }

    public void setProblemSlug(String problemSlug) {
        this.problemSlug = problemSlug;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
