package judgels.gabriel.engines.functional;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.SKIPPED;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.BlackboxGradingEngineIntegrationTests;
import judgels.gabriel.languages.java.JavaGradingLanguage;
import org.junit.jupiter.api.Test;

class FunctionalWithSubtasksGradingEngineJavaIntegrationTests extends BlackboxGradingEngineIntegrationTests {
    private static final FunctionalWithSubtasksGradingEngine ENGINE = new FunctionalWithSubtasksGradingEngine();
    private static final FunctionalWithSubtasksGradingConfig CONFIG = new FunctionalWithSubtasksGradingConfig.Builder()
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

    FunctionalWithSubtasksGradingEngineJavaIntegrationTests() {
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
                                testCaseResult(ACCEPTED, "✓", 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", 1, 2),
                                testCaseResult(ACCEPTED, "✓", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(ACCEPTED, "✓", 2),
                                testCaseResult(ACCEPTED, "✓", 2),
                                testCaseResult(ACCEPTED, "✓", 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, ACCEPTED, 70)));
    }

    @Test
    void wa_30() throws GradingException {
        setCustomSources(new JavaGradingLanguage(), "java-wa");

        addSourceFile("encoder", "encoder.java");
        addSourceFile("decoder", "decoder.java");
        assertResult(
                CONFIG,
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", 1, 2),
                                testCaseResult(ACCEPTED, "✓", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(WRONG_ANSWER, "✕", 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }

    @Test
    void wa_30_with_custom_scorer() throws GradingException {
        setCustomSources(new JavaGradingLanguage(), "java-wa");

        addSourceFile("encoder", "encoder.java");
        addSourceFile("decoder", "decoder.java");
        assertResult(
                new FunctionalWithSubtasksGradingConfig.Builder().from(CONFIG)
                        .customScorer("scorer-binary.cpp").build(),
                WRONG_ANSWER,
                30,
                ImmutableList.of(
                        testGroupResult(
                                0,
                                testCaseResult(ACCEPTED, "✓", 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", 0, 1, 2),
                                testCaseResult(ACCEPTED, "✓", 0, 2)),
                        testGroupResult(
                                1,
                                testCaseResult(ACCEPTED, "✓", 1, 2),
                                testCaseResult(ACCEPTED, "✓", 1, 2)),
                        testGroupResult(
                                2,
                                testCaseResult(WRONG_ANSWER, "✕", 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2),
                                testCaseResult(SKIPPED, "?", Optional.empty(), 2))),
                ImmutableList.of(
                        subtaskResult(1, ACCEPTED, 30),
                        subtaskResult(2, WRONG_ANSWER, 0)));
    }
}
