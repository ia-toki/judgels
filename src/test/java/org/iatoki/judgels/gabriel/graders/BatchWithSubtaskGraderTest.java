package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.CompilationVerdict;
import org.iatoki.judgels.gabriel.blackbox.EvaluationVerdict;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.SampleTestCase;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.SubtaskResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public final class BatchWithSubtaskGraderTest extends BlackBoxGraderTest {

    private final BatchWithSubtaskGrader grader;
    private final BatchWithSubtaskGradingConfig config;

    public BatchWithSubtaskGraderTest() {
        super("blackbox/batch");

        BatchWithSubtaskGradingConfig config = new BatchWithSubtaskGradingConfig();

        config.timeLimitInMilliseconds = 1000;
        config.memoryLimitInKilobytes = 65536;
        config.sampleTestData = ImmutableList.of(
                new SampleTestCase("sample_1.in", "sample_1.out", ImmutableSet.of(0)),
                new SampleTestCase("sample_2.in", "sample_2.out", ImmutableSet.of(0)),
                new SampleTestCase("sample_3.in", "sample_3.out", ImmutableSet.of(1))
        );

        config.testData = ImmutableList.of(
                new TestGroup(ImmutableList.of(
                        new TestCase("1_1.in", "1_1.out"),
                        new TestCase("1_2.in", "1_2.out")
                ), ImmutableSet.of(0, 1)),

                new TestGroup(ImmutableList.of(
                        new TestCase("2_1.in", "2_1.out"),
                        new TestCase("2_2.in", "2_2.out"),
                        new TestCase("2_3.in", "2_3.out")
                ), ImmutableSet.of(1))
        );

        config.subtaskPoints = ImmutableList.of(30, 70);

        this.config = config;
        this.grader = new BatchWithSubtaskGrader();
    }

    @Test
    public void testCompilationError() {
        addSourceFile("source", "aplusb-CE.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), CompilationVerdict.COMPILATION_ERROR);
            assertEquals(result.getScore(), 0);
            assertTrue(result.getDetails().getCompilationOutput().contains("'b'"));
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK() {
        addSourceFile("source", "aplusb-OK.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                    new SubtaskResult(ScoringVerdict.ACCEPTED, 30.0),
                    new SubtaskResult(ScoringVerdict.ACCEPTED, 70.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30() {
        addSourceFile("source", "aplusb-WA-at-2_3.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskResult(ScoringVerdict.ACCEPTED, 30.0),
                            new SubtaskResult(ScoringVerdict.WRONG_ANSWER, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30BecauseTLE() {
        addSourceFile("source", "aplusb-TLE-at-2_3.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskResult(ScoringVerdict.ACCEPTED, 30.0),
                            new SubtaskResult(EvaluationVerdict.TIME_LIMIT_EXCEEDED, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK30BecauseWAAtSample() {
        addSourceFile("source", "aplusb-WA-at-sample_3.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskResult(ScoringVerdict.ACCEPTED, 30.0),
                            new SubtaskResult(ScoringVerdict.WRONG_ANSWER, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }


    @Test
    public void testOK0() {
        addSourceFile("source", "aplusb-WA-at-1_1.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 0);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskResult(ScoringVerdict.WRONG_ANSWER, 0.0),
                            new SubtaskResult(ScoringVerdict.WRONG_ANSWER, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK100WithCustomScorer() {
        addSourceFile("source", "aplusb-OK-scorer.cpp");

        config.customScorer = "scorer.cpp";

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskResult(ScoringVerdict.ACCEPTED, 30.0),
                            new SubtaskResult(ScoringVerdict.ACCEPTED, 70.0))
            );
        } catch (GradingException e) {
            fail();
        }

        config.customScorer = null;
    }

    @Test
    public void testOK30WithCustomScorer() {
        addSourceFile("source", "aplusb-WA-at-2_3-scorer.cpp");

        config.customScorer = "scorer.cpp";

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), ScoringVerdict.OK);
            assertEquals(result.getScore(), 30);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskResult(ScoringVerdict.ACCEPTED, 30.0),
                            new SubtaskResult(ScoringVerdict.WRONG_ANSWER, 0.0))
            );
        } catch (GradingException e) {
            fail();
        }

        config.customScorer = null;
    }

    @Test
    public void testInternalErrorBecauseCustomScorerCE() {
        addSourceFile("source", "aplusb-OK-scorer.cpp");

        config.customScorer = "scorer-CE.cpp";

        try {
            runGrader(grader, config);
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof PreparationException);
            assertTrue(e.getMessage().contains("'fabs'"));
        }

        config.customScorer = null;
    }

    @Test
    public void testInternalErrorBecauseCustomScorerRTE() {
        addSourceFile("source", "aplusb-OK-scorer.cpp");

        config.customScorer = "scorer-RTE.cpp";

        try {
            runGrader(grader, config);
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof ScoringException);
        }

        config.customScorer = null;
    }

    @Test
    public void testInternalErrorBecauseCustomScorerOutputUnknownFormat() {
        addSourceFile("source", "aplusb-OK-scorer.cpp");

        config.customScorer = "scorer-WA.cpp";

        try {
            runGrader(grader, config);
            fail();
        } catch (GradingException e) {
            assertTrue(e instanceof ScoringException);
            assertTrue(e.getMessage().contains("unknown scoring format"));
        }

        config.customScorer = null;
    }
}
