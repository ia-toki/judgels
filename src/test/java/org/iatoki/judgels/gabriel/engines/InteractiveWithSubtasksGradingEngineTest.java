package org.iatoki.judgels.gabriel.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.InteractiveWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.languages.PlainCppGradingLanguage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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
                new TestGroup(0, ImmutableList.of(
                        new TestCase("sample_1.in", "sample_1.out", ImmutableSet.of(0, 1, 2)),
                        new TestCase("sample_2.in", "sample_2.out", ImmutableSet.of(0, 1, 2)),
                        new TestCase("sample_3.in", "sample_3.out", ImmutableSet.of(0, 2))
                )),
                new TestGroup(1, ImmutableList.of(
                        new TestCase("1_1.in", "1_1.out", ImmutableSet.of(1, 2)),
                        new TestCase("1_2.in", "1_2.out", ImmutableSet.of(1, 2))
                )),

                new TestGroup(2, ImmutableList.of(
                        new TestCase("2_1.in", "2_1.out", ImmutableSet.of(2)),
                        new TestCase("2_2.in", "2_2.out", ImmutableSet.of(2)),
                        new TestCase("2_3.in", "2_3.out", ImmutableSet.of(2))
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
            BlackBoxGradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_CE);
            assertEquals(result.getScore(), 0);
            assertTrue(result.getDetails().getCompilationOutputs().get("source").contains("strcmp"));
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
            BlackBoxGradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = result.getDetails();
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
            BlackBoxGradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK_WORST_WA);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = result.getDetails();
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
            BlackBoxGradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK_WORST_RTE);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_RTE, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    private InteractiveWithSubtasksGradingConfig createConfigWithCommunicator(String communicator) {
        return new InteractiveWithSubtasksGradingConfig(timeLimit, memoryLimit, testData, subtaskPoints, communicator);
    }
}
