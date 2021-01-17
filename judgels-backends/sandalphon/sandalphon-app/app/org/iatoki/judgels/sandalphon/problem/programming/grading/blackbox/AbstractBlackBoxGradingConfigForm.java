package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import java.util.List;
import play.data.validation.Constraints;

public abstract class AbstractBlackBoxGradingConfigForm {

    @Constraints.Required
    public int timeLimit;

    @Constraints.Required
    public int memoryLimit;

    public List<String> sampleTestCaseInputs;

    public List<String> sampleTestCaseOutputs;

    public List<List<String>> testCaseInputs;

    public List<List<String>> testCaseOutputs;

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public List<String> getSampleTestCaseInputs() {
        return sampleTestCaseInputs;
    }

    public void setSampleTestCaseInputs(List<String> sampleTestCaseInputs) {
        this.sampleTestCaseInputs = sampleTestCaseInputs;
    }

    public List<String> getSampleTestCaseOutputs() {
        return sampleTestCaseOutputs;
    }

    public void setSampleTestCaseOutputs(List<String> sampleTestCaseOutputs) {
        this.sampleTestCaseOutputs = sampleTestCaseOutputs;
    }

    public List<List<String>> getTestCaseInputs() {
        return testCaseInputs;
    }

    public void setTestCaseInputs(List<List<String>> testCaseInputs) {
        this.testCaseInputs = testCaseInputs;
    }

    public List<List<String>> getTestCaseOutputs() {
        return testCaseOutputs;
    }

    public void setTestCaseOutputs(List<List<String>> testCaseOutputs) {
        this.testCaseOutputs = testCaseOutputs;
    }
}
