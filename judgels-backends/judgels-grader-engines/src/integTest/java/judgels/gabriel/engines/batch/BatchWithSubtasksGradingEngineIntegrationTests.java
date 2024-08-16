package judgels.gabriel.engines.batch;

import static judgels.gabriel.api.SandboxExecutionStatus.TIMED_OUT;
import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.OK;
import static judgels.gabriel.api.Verdict.SKIPPED;
import static judgels.gabriel.api.Verdict.TIME_LIMIT_EXCEEDED;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import org.junit.jupiter.api.Test;

class BatchWithSubtasksGradingEngineIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final BatchWithSubtasksGradingEngine ENGINE = new BatchWithSubtasksGradingEngine();
    private static final BatchWithSubtasksGradingConfig CONFIG = new BatchWithSubtasksGradingConfig.Builder()
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

    BatchWithSubtasksGradingEngineIntegrationTests() {
        super("batch", ENGINE);
    }

    @Test
    void ac() throws GradingException {
        addSourceFile("source", "aplusb-AC.cpp");
        assertResult(
                CONFIG,
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, ACCEPTED, 70)));
    }

    @Test
    void wa_30() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(WRONG_ANSWER, "X", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void wa_30_at_sample() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-sample_3.cpp");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(WRONG_ANSWER, "X", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
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
    void tle_30() throws GradingException {
        addSourceFile("source", "aplusb-TLE-at-2_3.cpp");
        assertResult(
                CONFIG,
                TIME_LIMIT_EXCEEDED,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(TIME_LIMIT_EXCEEDED, "X", Optional.of(TIMED_OUT),  2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, TIME_LIMIT_EXCEEDED, 0)));
    }

    @Test
    void wa_0() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                0,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(WRONG_ANSWER, "X", 1, 2),
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
        addSourceFile("source", "aplusb-AC-scorer.cpp");
        assertResult(
                new BatchWithSubtasksGradingConfig.Builder().from(CONFIG).customScorer("scorer-binary.cpp").build(),
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, ACCEPTED, 70)));
    }

    @Test
    void ok_minimum_score_with_custom_scorer() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");
        assertResult(
                new BatchWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .customScorer("scorer-nonbinary-OK10.cpp").build(),
                OK,
                40,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(OK, "10", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, OK, 10)));
    }

    @Test
    void wa_30_with_custom_scorer() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");
        assertResult(
                new BatchWithSubtasksGradingConfig.Builder().from(CONFIG).customScorer("scorer-binary.cpp").build(),
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 1, 2),
                                testCaseResult(ACCEPTED, "*", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "*", 1, 2),
                                testCaseResult(ACCEPTED, "*", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(ACCEPTED, "*", 2),
                                testCaseResult(WRONG_ANSWER, "X", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void err_because_custom_scorer_ce() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");
        BatchWithSubtasksGradingConfig config =
                new BatchWithSubtasksGradingConfig.Builder().from(CONFIG).customScorer("scorer-CE.cpp").build();

        assertThatThrownBy(() -> runEngine(config))
                .isInstanceOf(PreparationException.class)
                .hasMessageContaining("fabs");
    }

    @Test
    void err_because_custom_scorer_rte() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");
        BatchWithSubtasksGradingConfig config =
                new BatchWithSubtasksGradingConfig.Builder().from(CONFIG).customScorer("scorer-RTE.cpp").build();

        assertThatThrownBy(() -> runEngine(config))
                .isInstanceOf(ScoringException.class);
    }

    @Test
    void err_because_custom_scorer_outputs_unknown_format() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");
        BatchWithSubtasksGradingConfig config =
                new BatchWithSubtasksGradingConfig.Builder().from(CONFIG).customScorer("scorer-WA.cpp").build();

        assertThatThrownBy(() -> runEngine(config))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("Unknown verdict");
    }
}
