package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

public abstract class MultipleSourceFilesBlackBoxGradingConfigForm extends AbstractBlackBoxGradingConfigForm {

    public String sourceFileFieldKeys;

    public String getSourceFileFieldKeys() {
        return sourceFileFieldKeys;
    }

    public void setSourceFileFieldKeys(String sourceFileFieldKeys) {
        this.sourceFileFieldKeys = sourceFileFieldKeys;
    }
}
