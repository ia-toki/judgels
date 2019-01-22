package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import java.util.List;

public class FunctionalWithSubtasksGradingConfigForm extends MultipleSourceFilesBlackBoxGradingConfigForm {

    public List<List<Integer>> sampleTestCaseSubtaskIds;

    public List<List<Integer>> testGroupSubtaskIds;

    public List<Integer> subtaskPoints;

    public String customScorer;

    public List<List<Integer>> getSampleTestCaseSubtaskIds() {
        return sampleTestCaseSubtaskIds;
    }

    public void setSampleTestCaseSubtaskIds(List<List<Integer>> sampleTestCaseSubtaskIds) {
        this.sampleTestCaseSubtaskIds = sampleTestCaseSubtaskIds;
    }

    public List<List<Integer>> getTestGroupSubtaskIds() {
        return testGroupSubtaskIds;
    }

    public void setTestGroupSubtaskIds(List<List<Integer>> testGroupSubtaskIds) {
        this.testGroupSubtaskIds = testGroupSubtaskIds;
    }

    public List<Integer> getSubtaskPoints() {
        return subtaskPoints;
    }

    public void setSubtaskPoints(List<Integer> subtaskPoints) {
        this.subtaskPoints = subtaskPoints;
    }

    public String getCustomScorer() {
        return customScorer;
    }

    public void setCustomScorer(String customScorer) {
        this.customScorer = customScorer;
    }
}
