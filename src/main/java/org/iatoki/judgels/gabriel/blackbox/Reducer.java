package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public interface Reducer {
    SubtaskResult reduceTestCases(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException;

    ReductionResult reduceSubtasks(List<SubtaskResult> subtaskResults) throws ReductionException;
}
