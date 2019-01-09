package judgels.gabriel.api;

import java.util.List;

public interface Reducer {
    SubtaskRawResult reduceTestCaseResults(List<TestCaseRawResult> testCaseResults, Subtask subtask)
            throws ReductionException;
    List<TestCaseRawResult> improveTestCaseResults(List<TestCaseRawResult> testCaseResults, Subtask subtask);
    ReductionResult reduceSubtaskResults(List<SubtaskRawResult> subtaskResults) throws ReductionException;
}
