package org.iatoki.judgels.sandalphon.problem.bundle.item;

import play.data.validation.Constraints;

public final class ItemEssayConfForm {
    @Constraints.Required
    public String meta;

    @Constraints.Required
    public String statement;

    @Constraints.Required
    public Double score;

    public ItemEssayConfForm() {
        score = 1.0;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
