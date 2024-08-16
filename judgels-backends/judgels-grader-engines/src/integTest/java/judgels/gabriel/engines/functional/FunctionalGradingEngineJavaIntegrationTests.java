package judgels.gabriel.engines.functional;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.COMPILATION_ERROR;
import static judgels.gabriel.api.Verdict.OK;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import judgels.gabriel.languages.java.JavaGradingLanguage;
import org.junit.jupiter.api.Test;

class FunctionalGradingEngineJavaIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final FunctionalGradingEngine ENGINE = new FunctionalGradingEngine();
    private static final FunctionalGradingConfig CONFIG = new FunctionalGradingConfig.Builder()
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

    FunctionalGradingEngineJavaIntegrationTests() {
        super("functional", ENGINE);
    }

    @Test
    void ac() throws GradingException {
        setCustomSources(new JavaGradingLanguage(), "java-ac");

        addSourceFile("encoder", "encoder.java");
        addSourceFile("decoder", "decoder.java");
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
    void wa_80() throws GradingException {
        setCustomSources(new JavaGradingLanguage(), "java-wa");

        addSourceFile("encoder", "encoder.java");
        addSourceFile("decoder", "decoder.java");
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
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(WRONG_ANSWER, "0", -1),
                                testCaseResult(ACCEPTED, "20", -1),
                                testCaseResult(ACCEPTED, "20", -1))),
                ImmutableList.of(
                        subtaskResult(-1, WRONG_ANSWER, 80)));
    }

    @Test
    void ok_90_with_custom_scorer() throws GradingException {
        setCustomSources(new JavaGradingLanguage(), "java-ac");

        addSourceFile("encoder", "encoder.java");
        addSourceFile("decoder", "decoder.java");
        assertResult(
                new FunctionalGradingConfig.Builder().from(CONFIG)
                        .customScorer("scorer-nonbinary-OK10-at-1_1.cpp").build(),
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
        setCustomSources(new JavaGradingLanguage(), "java-ce");

        addSourceFile("encoder", "encoder.java");
        addSourceFile("decoder", "decoder.java");

        GradingResult result = runEngine(CONFIG);
        assertThat(result.getVerdict()).isEqualTo(COMPILATION_ERROR);
        assertThat(result.getScore()).isEqualTo(0);

        GradingResultDetails details = getDetails(result);
        assertThat(details.getCompilationOutputs().get("source")).contains("bogus");
        assertThat(details.getCompilationOutputs().get("source")).contains("encoder.java");
    }
}
