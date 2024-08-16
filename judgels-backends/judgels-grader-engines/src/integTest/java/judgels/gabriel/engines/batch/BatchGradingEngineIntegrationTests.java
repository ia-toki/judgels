package judgels.gabriel.engines.batch;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.COMPILATION_ERROR;
import static judgels.gabriel.api.Verdict.OK;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingOptions;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestCaseResult;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import org.junit.jupiter.api.Test;

class BatchGradingEngineIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final BatchGradingEngine ENGINE = new BatchGradingEngine();
    private static final BatchGradingConfig CONFIG = new BatchGradingConfig.Builder()
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

    BatchGradingEngineIntegrationTests() {
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
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1))),
                ImmutableList.of(
                        subtaskResult(-1, ACCEPTED, 100)));
    }

    @Test
    void ac_but_wa_at_sample_that_is_not_included() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-sample_3.cpp");
        assertResult(
                CONFIG,
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(WRONG_ANSWER, "", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1))),
                ImmutableList.of(
                        subtaskResult(-1, ACCEPTED, 100)));
    }

    @Test
    void wa_80() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                80,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(WRONG_ANSWER, "0", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1))),
                ImmutableList.of(
                        subtaskResult(-1, WRONG_ANSWER, 80)));
    }

    @Test
    void wa_token_mismatch() throws GradingException {
        addSourceFile("source", "aplusb-WA-token-mismatch.cpp");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                0,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(WRONG_ANSWER, "", 0),
                                testCaseResult(WRONG_ANSWER, "", 0),
                                testCaseResult(WRONG_ANSWER, "", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(WRONG_ANSWER, "0", -1),
                                testCaseResult(WRONG_ANSWER, "0", -1),
                                testCaseResult(WRONG_ANSWER, "0", -1),
                                testCaseResult(WRONG_ANSWER, "0", -1),
                                testCaseResult(WRONG_ANSWER, "0", -1))),
                ImmutableList.of(
                        subtaskResult(-1, WRONG_ANSWER, 0)));
    }

    @Test
    void ok_90_with_custom_scorer() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");
        assertResult(
                new BatchGradingConfig.Builder().from(CONFIG).customScorer("scorer-nonbinary-OK10.cpp").build(),
                OK,
                90,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0),
                                testCaseResult(ACCEPTED, "", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(OK, "10", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1))),
                ImmutableList.of(
                        subtaskResult(-1, OK, 90)));
    }

    @Test
    void ce() throws GradingException {
        addSourceFile("source", "aplusb-CE.cpp");

        GradingResult result = runEngine(CONFIG);
        assertThat(result.getVerdict()).isEqualTo(COMPILATION_ERROR);
        assertThat(result.getScore()).isEqualTo(0);

        GradingResultDetails details = getDetails(result);
        assertThat(details.getCompilationOutputs().get("source")).contains("BB");
    }

    @Test
    void with_revealed_evaluation_result() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");

        BatchGradingConfig config = new BatchGradingConfig.Builder()
                .from(ENGINE.createDefaultConfig())
                .testData(List.of(
                        TestGroup.of(0, List.of()),
                        TestGroup.of(-1, List.of(
                                TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(-1)),
                                TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(-1)),
                                TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(-1))))))
                .build();
        GradingOptions options = new GradingOptions.Builder().shouldRevealEvaluation(true).build();

        GradingResult result = runEngine(config, options);
        GradingResultDetails details = getDetails(result);

        List<TestCaseResult> testCaseResults = details.getTestDataResults().get(1).getTestCaseResults();
        assertThat(testCaseResults.get(0).getRevealedInput()).contains("1 1\n");
        assertThat(testCaseResults.get(0).getRevealedSolutionOutput()).contains("Case #1:\n-1\n");
        assertThat(testCaseResults.get(1).getRevealedInput()).contains("1 2\n");
        assertThat(testCaseResults.get(1).getRevealedSolutionOutput()).contains("Case #1:\n3\n");
        assertThat(testCaseResults.get(2).getRevealedInput()).contains("2 1\n");
        assertThat(testCaseResults.get(2).getRevealedSolutionOutput()).contains("Case #1:\n3\n");
    }
}
