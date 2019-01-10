package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import java.util.List;

public class FunctionalWithSubtasksGradingConfigForm extends MultipleSourceFilesBlackBoxGradingConfigForm {

    public List<List<Integer>> sampleTestCaseSubtaskIds;

    public List<List<Integer>> testGroupSubtaskIds;

    public List<Integer> subtaskPoints;

    public String customScorer;
}
