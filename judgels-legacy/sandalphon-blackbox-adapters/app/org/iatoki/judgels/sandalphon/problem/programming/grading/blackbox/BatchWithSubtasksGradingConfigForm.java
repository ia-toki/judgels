package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

public final class BatchWithSubtasksGradingConfigForm extends SingleSourceFileWithSubtasksBlackBoxGradingConfigForm {

    public String customScorer;

    public String getCustomScorer() {
        return customScorer;
    }

    public void setCustomScorer(String customScorer) {
        this.customScorer = customScorer;
    }
}
