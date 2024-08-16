package judgels.gabriel.engines.outputonly;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.OK;
import static judgels.gabriel.api.Verdict.SKIPPED;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import org.junit.jupiter.api.Test;

class OutputOnlyWithSubtasksGradingEngineIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final OutputOnlyWithSubtasksGradingEngine ENGINE = new OutputOnlyWithSubtasksGradingEngine();
    private static final OutputOnlyWithSubtasksGradingConfig CONFIG = new OutputOnlyWithSubtasksGradingConfig.Builder()
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

    OutputOnlyWithSubtasksGradingEngineIntegrationTests() {
        super("outputonly", ENGINE);
    }

    @Test
    void ac() throws GradingException {
        addSourceFile("source", "AC.zip");
        assertResult(
                CONFIG,
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, ACCEPTED, 70)));
    }

    @Test
    void wa_30() throws GradingException {
        addSourceFile("source", "WA-at-2_3.zip");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(WRONG_ANSWER, "✕", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void wa_30_at_sample() throws GradingException {
        addSourceFile("source", "WA-at-sample_3.zip");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(WRONG_ANSWER, "✕", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void wa_30_because_some_files_missing() throws GradingException {
        addSourceFile("source", "WA-missing-at-2_3.zip");
        assertResult(
                CONFIG,
                OK,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, OK, 0)));
    }

    @Test
    void wa_0() throws GradingException {
        addSourceFile("source", "WA-at-1_1.zip");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                0,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(WRONG_ANSWER, "✕", Optional.empty(), 1, 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, WRONG_ANSWER, 0),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void ac_with_custom_scorer() throws GradingException {
        addSourceFile("source", "AC-scorer.zip");
        assertResult(
                new OutputOnlyWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .customScorer("scorer-binary.cpp").build(),
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, ACCEPTED, 70)));
    }

    @Test
    void wa_30_with_custom_scorer() throws GradingException {
        addSourceFile("source", "WA-at-2_3-scorer.zip");
        assertResult(
                new OutputOnlyWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .customScorer("scorer-binary.cpp").build(),
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(ACCEPTED, "✓", Optional.empty(), 2),
                                testCaseResult(WRONG_ANSWER, "✕", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }
}
