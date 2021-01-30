package org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer;

import play.data.validation.Constraints;

public final class ShortAnswerItemConfigForm {
    @Constraints.Required
    public String meta;

    @Constraints.Required
    public String statement;

    @Constraints.Required
    public Double score;

    @Constraints.Required
    public Double penalty;

    @Constraints.Required
    public String inputValidationRegex;

    public String gradingRegex;

    public ShortAnswerItemConfigForm() {
        score = 1.0;
        penalty = 0.0;
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

    public Double getPenalty() {
        return penalty;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }

    public String getInputValidationRegex() {
        return inputValidationRegex;
    }

    public void setInputValidationRegex(String inputValidationRegex) {
        this.inputValidationRegex = inputValidationRegex;
    }

    public String getGradingRegex() {
        return gradingRegex;
    }

    public void setGradingRegex(String gradingRegex) {
        this.gradingRegex = gradingRegex;
    }
}
