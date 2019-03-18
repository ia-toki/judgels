package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public final class InteractiveWithSubtasksGradingEngineTest extends BlackBoxGradingEngineTest {

    private final InteractiveWithSubtasksGradingEngine engine;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;
    private final List<Integer> subtaskPoints;

    public InteractiveWithSubtasksGradingEngineTest() {
        super("interactive");

        this.timeLimit = 1000;
        this.memoryLimit = 65536;

        this.testData = ImmutableList.of(
                TestGroup.of(0, ImmutableList.of(
                        TestCase.of("sample_1.in", "sample_1.out", ImmutableSet.of(0, 1, 2)),
                        TestCase.of("sample_2.in", "sample_2.out", ImmutableSet.of(0, 1, 2)),
                        TestCase.of("sample_3.in", "sample_3.out", ImmutableSet.of(0, 2))
                )),
                TestGroup.of(1, ImmutableList.of(
                        TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(1, 2)),
                        TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(1, 2))
                )),

                TestGroup.of(2, ImmutableList.of(
                        TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(2)),
                        TestCase.of("2_2.in", "2_2.out", ImmutableSet.of(2)),
                        TestCase.of("2_3.in", "2_3.out", ImmutableSet.of(2))
                ))
        );

        this.subtaskPoints = ImmutableList.of(30, 70);

        this.engine = new InteractiveWithSubtasksGradingEngine();
        this.engine.setCommunicatorLanguage(new PlainCppGradingLanguage());
    }

    @Test
    public void testCE() {
        addSourceFile("source", "binsearch-CE.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_CE);
            assertEquals(result.getScore(), 0);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertTrue(details.getCompilationOutputs().get("source").contains("strcmp"));
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testInternalErrorBecauseCommunicatorCE() {
        addSourceFile("source", "binsearch-OK.cpp");

        try {
            runEngine(engine, createConfigWithCommunicator("communicator-CE.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof PreparationException);
            assertTrue(e.getMessage().contains("exit"));
        }
    }

    @Test
    public void testInternalErrorBecauseCommunicatorRTE() {
        addSourceFile("source", "binsearch-OK.cpp");

        try {
            runEngine(engine, createConfigWithCommunicator("communicator-RTE.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof EvaluationException);
        }
    }

    @Test
    public void testInternalErrorBecauseCommunicatorNotSpecified() {
        addSourceFile("source", "binsearch-OK.cpp");

        try {
            runEngine(engine, createConfigWithCommunicator(null));
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof PreparationException);
            assertTrue(e.getMessage().contains("Communicator not specified"));
        }
    }

    @Test
    public void testAC() {
        addSourceFile("source", "binsearch-OK.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_AC, 70.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30() {
        addSourceFile("source", "linsearch-WA-at-subtask_2.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK_WORST_WA);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_WA, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }


    @Test
    public void testOK30BecauseRTE() {
        addSourceFile("source", "linsearch-RTE-at-subtask_2.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK_WORST_RTE);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_RTE, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    private InteractiveWithSubtasksGradingConfig createConfigWithCommunicator(String communicator) {
        return new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .subtaskPoints(subtaskPoints)
                .communicator(Optional.ofNullable(communicator))
                .build();
    }
}
