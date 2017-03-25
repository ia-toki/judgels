package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.BatchWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public final class BatchWithSubtasksGradingEngineTest extends BlackBoxGradingEngineTest {

    private final BatchWithSubtasksGradingEngine engine;
    private final BatchWithSubtasksGradingConfig config;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;
    private final List<Integer> subtaskPoints;

    public BatchWithSubtasksGradingEngineTest() {
        super("batch");

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

        this.config = new BatchWithSubtasksGradingConfig(timeLimit, memoryLimit, testData, subtaskPoints, null);
        this.engine = new BatchWithSubtasksGradingEngine();
        this.engine.setScorerLanguage(new PlainCppGradingLanguage());
    }

    @Test
    public void testCE() {
        addSourceFile("source", "aplusb-CE.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_CE);
            assertEquals(result.getScore(), 0);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertTrue(details.getCompilationOutputs().get("source").contains("BB"));
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testAC() {
        addSourceFile("source", "aplusb-AC.cpp");

        try {
            GradingResult result = runEngine(engine, config);
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
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");

        try {
            GradingResult result = runEngine(engine, config);
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
    public void testOK30BecauseTLE() {
        addSourceFile("source", "aplusb-TLE-at-2_3.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_OK_WORST_TLE);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                            new SubtaskFinalResult(2, VERDICT_TLE, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30BecauseWAAtSample() {
        addSourceFile("source", "aplusb-WA-at-sample_3.cpp");

        try {
            GradingResult result = runEngine(engine, config);
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
    public void testOK0() {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_OK_WORST_WA);
            assertEquals(result.getScore(), 0);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(1, VERDICT_WA, 0.0),
                            new SubtaskFinalResult(2, VERDICT_WA, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOKWithMinimumScore() throws GradingException {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");
        GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-nonbinary-OK10.cpp"));
        assertEquals(result.getVerdict(), VERDICT_OK);
        assertEquals(result.getScore(), 40);

        BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
        assertEquals(details.getSubtaskResults(), ImmutableList.of(
                new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                new SubtaskFinalResult(2, VERDICT_OK, 10.0))
        );
    }

    @Test
    public void testACWithCustomScorer() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-binary.cpp"));
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
    public void testOK30WithCustomScorer() {
        addSourceFile("source", "aplusb-WA-at-2_3-scorer.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-binary.cpp"));
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
    public void testInternalErrorBecauseCustomScorerCE() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            runEngine(engine, createConfigWithCustomScorer("scorer-CE.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof PreparationException);
            assertTrue(e.getMessage().contains("fabs"));
        }
    }

    @Test
    public void testInternalErrorBecauseCustomScorerRTE() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            runEngine(engine, createConfigWithCustomScorer("scorer-RTE.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof ScoringException);
        }
    }

    @Test
    public void testInternalErrorBecauseCustomScorerOutputUnknownFormat() {
        addSourceFile("source", "aplusb-AC-scorer.cpp");

        try {
            runEngine(engine, createConfigWithCustomScorer("scorer-WA.cpp"));
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof ScoringException);
            assertTrue(e.getMessage().contains("Unknown scoring format"));
        }
    }

    private BatchWithSubtasksGradingConfig createConfigWithCustomScorer(String customScorer) {
        return new BatchWithSubtasksGradingConfig(timeLimit, memoryLimit, testData, subtaskPoints, customScorer);
    }
}
