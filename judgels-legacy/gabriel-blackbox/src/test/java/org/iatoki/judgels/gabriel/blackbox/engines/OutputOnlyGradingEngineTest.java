package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.OutputOnlyGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public final class OutputOnlyGradingEngineTest extends BlackBoxGradingEngineTest {

    private final OutputOnlyGradingEngine engine;
    private final OutputOnlyGradingConfig config;

    private final List<TestGroup> testData;

    public OutputOnlyGradingEngineTest() {
        super("outputonly");

        this.testData = ImmutableList.of(
                new TestGroup(0, ImmutableList.of(
                        new TestCase("sample_1.in", "sample_1.out", ImmutableSet.of(0)),
                        new TestCase("sample_2.in", "sample_2.out", ImmutableSet.of(0)),
                        new TestCase("sample_3.in", "sample_3.out", ImmutableSet.of(0))
                )),
                new TestGroup(-1, ImmutableList.of(
                        new TestCase("1_1.in", "1_1.out", ImmutableSet.of(-1)),
                        new TestCase("1_2.in", "1_2.out", ImmutableSet.of(-1)),
                        new TestCase("2_1.in", "2_1.out", ImmutableSet.of(-1)),
                        new TestCase("2_2.in", "2_2.out", ImmutableSet.of(-1)),
                        new TestCase("2_3.in", "2_3.out", ImmutableSet.of(-1))
                ))
        );

        this.config = new OutputOnlyGradingConfig(testData, null);
        this.engine = new OutputOnlyGradingEngine();
        this.engine.setScorerLanguage(new PlainCppGradingLanguage());
    }

    @Test
    public void testAC() {
        addSourceFile("source", "AC.zip");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_AC, 100))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testACButWAAtSampleThatIsNotIncluded() {
        addSourceFile("source", "WA-at-sample_3.zip");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_AC, 100))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testWA80BecauseSomeOutputFilesAreMissing() {
        addSourceFile("source", "WA-missing-at-2_3.zip");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 80);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                    new SubtaskFinalResult(-1, VERDICT_WA, 80))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testWA80() {
        addSourceFile("source", "WA-at-1_1.zip");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 80);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_WA, 80))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK90WithCustomScorer() {
        addSourceFile("source", "WA-at-1_1.zip");

        try {
            GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-nonbinary-OK10-at-1_1.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK);
            assertEquals(result.getScore(), 90);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                    new SubtaskFinalResult(-1, VERDICT_OK, 90))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    private OutputOnlyGradingConfig createConfigWithCustomScorer(String customScorer) {
        return new OutputOnlyGradingConfig(testData, customScorer);
    }
}
