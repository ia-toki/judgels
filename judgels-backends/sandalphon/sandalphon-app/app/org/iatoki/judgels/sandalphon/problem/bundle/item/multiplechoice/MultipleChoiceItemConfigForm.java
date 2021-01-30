package org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice;

import java.util.List;
import play.data.validation.Constraints;

public final class MultipleChoiceItemConfigForm {

    @Constraints.Required
    public String meta;

    @Constraints.Required
    public String statement;

    @Constraints.Required
    public Double score;

    @Constraints.Required
    public Double penalty;

    public List<String> choiceAliases;

    public List<String> choiceContents;

    public List<Boolean> isCorrects;

    public MultipleChoiceItemConfigForm() {
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

    public List<String> getChoiceAliases() {
        return choiceAliases;
    }

    public void setChoiceAliases(List<String> choiceAliases) {
        this.choiceAliases = choiceAliases;
    }

    public List<String> getChoiceContents() {
        return choiceContents;
    }

    public void setChoiceContents(List<String> choiceContents) {
        this.choiceContents = choiceContents;
    }

    public List<Boolean> getIsCorrects() {
        return isCorrects;
    }

    public void setIsCorrects(List<Boolean> isCorrects) {
        this.isCorrects = isCorrects;
    }
}
