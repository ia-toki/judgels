package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import java.util.List;

public abstract class SingleSourceFileWithSubtasksBlackBoxGradingConfigForm extends SingleSourceFileBlackBoxGradingConfigForm {

    public List<List<Integer>> sampleTestCaseSubtaskIds;

    public List<List<Integer>> testGroupSubtaskIds;

    public List<Integer> subtaskPoints;
}
