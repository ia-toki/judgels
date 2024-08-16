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

class OutputOnlyGradingEngineIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final OutputOnlyGradingEngine ENGINE = new OutputOnlyGradingEngine();
    private static final OutputOnlyGradingConfig CONFIG = new OutputOnlyGradingConfig.Builder()
            .from(ENGINE.createDefaultConfig())
            .testData(ImmutableList.of(
                    TestGroup.of(0, ImmutableList.of(
                            TestCase.of("sample_1.in", "sample_1.out", ImmutableSet.of(0)),
                            TestCase.of("sample_2.in", "sample_2.out", ImmutableSet.of(0)),
                            TestCase.of("sample_3.in", "sample_3.out", ImmutableSet.of(0)))),
                    TestGroup.of(-1, ImmutableList.of(
                            TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(-1)),
                            TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(-1)),
                            TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(-1)),
                            TestCase.of("2_2.in", "2_2.out", ImmutableSet.of(-1)),
                            TestCase.of("2_3.in", "2_3.out", ImmutableSet.of(-1))))))
            .build();

    OutputOnlyGradingEngineIntegrationTests() {
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
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1))),
                ImmutableList.of(
                        subtaskResult(-1, ACCEPTED, 100)));
    }

    @Test
    void ac_but_wa_at_sample_that_is_not_included() throws GradingException {
        addSourceFile("source", "WA-at-sample_3.zip");
        assertResult(
                CONFIG,
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(WRONG_ANSWER, "", Optional.empty(), 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1))),
                ImmutableList.of(
                        subtaskResult(-1, ACCEPTED, 100)));
    }

    @Test
    void wa_80() throws GradingException {
        addSourceFile("source", "WA-at-1_1.zip");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                80,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(WRONG_ANSWER, "0", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1))),
                ImmutableList.of(
                        subtaskResult(-1, WRONG_ANSWER, 80)));
    }

    @Test
    void ok_80_because_some_output_files_missing() throws GradingException {
        addSourceFile("source", "WA-missing-at-2_3.zip");
        assertResult(
                CONFIG,
                OK,
                80,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(SKIPPED, "0", Optional.empty(), -1))),
                ImmutableList.of(
                        subtaskResult(-1, OK, 80)));
    }

    @Test
    void ok_90_with_custom_scorer() throws GradingException {
        addSourceFile("source", "WA-at-1_1.zip");
        assertResult(
                new OutputOnlyGradingConfig.Builder().from(CONFIG)
                        .customScorer("scorer-nonbinary-OK10-at-1_1.cpp").build(),
                OK,
                90,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0),
                                testCaseResult(ACCEPTED, "", Optional.empty(), 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(OK, "10", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1),
                                testCaseResult(ACCEPTED, "20", Optional.empty(), -1))),
                ImmutableList.of(
                        subtaskResult(-1, OK, 90)));
    }
}
