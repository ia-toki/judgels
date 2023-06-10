package judgels.gabriel.engines.interactive;

import static judgels.gabriel.api.SandboxExecutionStatus.NONZERO_EXIT_CODE;
import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.RUNTIME_ERROR;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import org.junit.jupiter.api.Test;

class InteractiveWithSubtasksGradingEngineIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final InteractiveWithSubtasksGradingEngine ENGINE = new InteractiveWithSubtasksGradingEngine();
    private static final InteractiveWithSubtasksGradingConfig CONFIG = new InteractiveWithSubtasksGradingConfig
            .Builder()
            .from(ENGINE.createDefaultConfig())
            .testData(ImmutableList.of(
                    TestGroup.of(0, ImmutableList.of(
                            TestCase.of("sample_1.in", "sample_1.out", ImmutableSet.of(0, 1, 2)),
                            TestCase.of("sample_2.in", "sample_2.out", ImmutableSet.of(0, 1, 2)),
                            TestCase.of("sample_3.in", "sample_3.out", ImmutableSet.of(0, 2)))),
                    TestGroup.of(1, ImmutableList.of(
                            TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(1, 2)),
                            TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(1, 2)))),
                    TestGroup.of(2, ImmutableList.of(
                            TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(2)),
                            TestCase.of("2_2.in", "2_2.out", ImmutableSet.of(2)),
                            TestCase.of("2_3.in", "2_3.out", ImmutableSet.of(2))))))
            .subtaskPoints(ImmutableList.of(30, 70))
            .build();

    InteractiveWithSubtasksGradingEngineIntegrationTests() {
        super("interactive", ENGINE);
    }

    @Test
    void ac() throws GradingException {
        addSourceFile("source", "binsearch-OK.cpp");
        assertResult(
                new InteractiveWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .communicator("communicator-binary.cpp").build(),
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "* [8]", 0, 1, 2),
                                testCaseResult(ACCEPTED, "* [9]", 0, 1, 2),
                                testCaseResult(ACCEPTED, "* [10]", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "* [9]", 1, 2),
                                testCaseResult(ACCEPTED, "* [10]", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "* [10]", 2),
                                testCaseResult(ACCEPTED, "* [9]", 2),
                                testCaseResult(ACCEPTED, "* [1]", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, ACCEPTED, 70)));
    }

    @Test
    void wa_30() throws GradingException {
        addSourceFile("source", "linsearch-WA-at-subtask_2.cpp");
        assertResult(
                new InteractiveWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .communicator("communicator-binary.cpp").build(),
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "* [3]", 0, 1, 2),
                                testCaseResult(ACCEPTED, "* [5]", 0, 1, 2),
                                testCaseResult(WRONG_ANSWER, "X [11]", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "* [1]", 1, 2),
                                testCaseResult(ACCEPTED, "* [8]", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(WRONG_ANSWER, "X [11]", 2),
                                testCaseResult(WRONG_ANSWER, "X [11]", 2),
                                testCaseResult(WRONG_ANSWER, "X [11]", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void rte_30() throws GradingException {
        addSourceFile("source", "linsearch-RTE-at-subtask_2.cpp");
        assertResult(
                new InteractiveWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .communicator("communicator-binary.cpp").build(),
                RUNTIME_ERROR,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "* [3]", 0, 1, 2),
                                testCaseResult(ACCEPTED, "* [5]", 0, 1, 2),
                                testCaseResult(RUNTIME_ERROR, "X", Optional.of(NONZERO_EXIT_CODE), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "* [1]", 1, 2),
                                testCaseResult(ACCEPTED, "* [8]", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(RUNTIME_ERROR, "X", Optional.of(NONZERO_EXIT_CODE), 2),
                                testCaseResult(RUNTIME_ERROR, "X", Optional.of(NONZERO_EXIT_CODE), 2),
                                testCaseResult(RUNTIME_ERROR, "X", Optional.of(NONZERO_EXIT_CODE), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, RUNTIME_ERROR, 0)));
    }
}
