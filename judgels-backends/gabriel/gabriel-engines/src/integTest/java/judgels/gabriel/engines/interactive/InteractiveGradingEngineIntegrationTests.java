package judgels.gabriel.engines.interactive;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.COMPILATION_ERROR;
import static judgels.gabriel.api.Verdict.OK;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import org.junit.jupiter.api.Test;

class InteractiveGradingEngineIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final InteractiveGradingEngine ENGINE = new InteractiveGradingEngine();
    private static final InteractiveGradingConfig CONFIG = new InteractiveGradingConfig.Builder()
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

    InteractiveGradingEngineIntegrationTests() {
        super("interactive", ENGINE);
    }

    @Test
    void ac() throws GradingException {
        addSourceFile("source", "binsearch-OK.cpp");
        assertResult(
                new InteractiveGradingConfig.Builder().from(CONFIG).communicator("communicator-binary.cpp").build(),
                ACCEPTED,
                100,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "[8]", 0),
                                testCaseResult(ACCEPTED, "[9]", 0),
                                testCaseResult(ACCEPTED, "[10]", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20.0 [9]", -1),
                                testCaseResult(ACCEPTED, "20.0 [10]", -1),
                                testCaseResult(ACCEPTED, "20.0 [10]", -1),
                                testCaseResult(ACCEPTED, "20.0 [9]", -1),
                                testCaseResult(ACCEPTED, "20.0 [1]", -1))),
                ImmutableList.of(
                        subtaskResult(-1, ACCEPTED, 100)));
    }

    @Test
    void wa_40() throws GradingException {
        addSourceFile("source", "linsearch-WA-at-subtask_2.cpp");
        assertResult(
                new InteractiveGradingConfig.Builder().from(CONFIG).communicator("communicator-binary.cpp").build(),
                WRONG_ANSWER,
                40,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "[3]", 0),
                                testCaseResult(ACCEPTED, "[5]", 0),
                                testCaseResult(WRONG_ANSWER, "[11]", 0)),
                        testGroupResult(
                                -1,
                                testCaseResult(ACCEPTED, "20.0 [1]", -1),
                                testCaseResult(ACCEPTED, "20.0 [8]", -1),
                                testCaseResult(WRONG_ANSWER, "0.0 [11]", -1),
                                testCaseResult(WRONG_ANSWER, "0.0 [11]", -1),
                                testCaseResult(WRONG_ANSWER, "0.0 [11]", -1))),
                ImmutableList.of(
                        subtaskResult(-1, WRONG_ANSWER, 40)));
    }

    @Test
    void wa_90() throws GradingException {
        addSourceFile("source", "binsearch-OK.cpp");
        assertResult(
                new InteractiveGradingConfig.Builder().from(CONFIG)
                        .communicator("communicator-nonbinary-OK10-at-1_1.cpp").build(),
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
                                testCaseResult(OK, "10.0", -1),
                                testCaseResult(ACCEPTED, "20.0", -1),
                                testCaseResult(ACCEPTED, "20.0", -1),
                                testCaseResult(ACCEPTED, "20.0", -1),
                                testCaseResult(ACCEPTED, "20.0", -1))),
                ImmutableList.of(
                        subtaskResult(-1, OK, 90)));
    }

    @Test
    void ce() throws GradingException {
        addSourceFile("source", "binsearch-CE.cpp");

        InteractiveGradingConfig config = new InteractiveGradingConfig.Builder().from(CONFIG)
                .communicator("communicator-binary.cpp").build();
        GradingResult result = runEngine(config);
        assertThat(result.getVerdict()).isEqualTo(COMPILATION_ERROR);
        assertThat(result.getScore()).isEqualTo(0);

        GradingResultDetails details = getDetails(result);
        assertThat(details.getCompilationOutputs().get("source")).contains("strcmp");
    }

    @Test
    void err_because_communicator_ce() {
        addSourceFile("source", "binsearch-OK.cpp");
        InteractiveGradingConfig config =
                new InteractiveGradingConfig.Builder().from(CONFIG).communicator("communicator-CE.cpp").build();

        assertThatThrownBy(() -> runEngine(config))
                .isInstanceOf(PreparationException.class)
                .hasMessageContaining("exit");
    }

    @Test
    void err_because_communicator_rte() {
        addSourceFile("source", "binsearch-OK.cpp");
        InteractiveGradingConfig config =
                new InteractiveGradingConfig.Builder().from(CONFIG).communicator("communicator-RTE.cpp").build();

        assertThatThrownBy(() -> runEngine(config))
                .isInstanceOf(EvaluationException.class);
    }

    @Test
    void err_because_communicator_not_specified() {
        addSourceFile("source", "binsearch-OK.cpp");
        assertThatThrownBy(() -> runEngine(CONFIG))
                .isInstanceOf(PreparationException.class)
                .hasMessageContaining("Communicator not specified");
    }
}
