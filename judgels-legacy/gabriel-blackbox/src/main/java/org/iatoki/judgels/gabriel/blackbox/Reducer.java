package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public interface Reducer {
    SubtaskResult reduceTestCaseResults(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException;

    List<TestCaseResult> improveTestCaseResults(List<TestCaseResult> testCaseResults, Subtask subtask);

    ReductionResult reduceSubtaskResults(List<SubtaskResult> subtaskResults) throws ReductionException;
}
