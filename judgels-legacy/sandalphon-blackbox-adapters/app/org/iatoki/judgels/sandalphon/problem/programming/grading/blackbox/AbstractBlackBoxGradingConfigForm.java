package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import play.data.validation.Constraints;

import java.util.List;

public abstract class AbstractBlackBoxGradingConfigForm {

    @Constraints.Required
    public int timeLimit;

    @Constraints.Required
    public int memoryLimit;

    public List<String> sampleTestCaseInputs;

    public List<String> sampleTestCaseOutputs;

    public List<List<String>> testCaseInputs;

    public List<List<String>> testCaseOutputs;
}
