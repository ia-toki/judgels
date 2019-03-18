package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalGradingConfig;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class FunctionalGradingEngineTest extends BlackBoxGradingEngineTest {
    private final FunctionalGradingEngine engine;
    private final FunctionalGradingConfig config;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;

    public FunctionalGradingEngineTest() {
        super("functional");

        this.timeLimit = 1000;
        this.memoryLimit = 65536;

        this.testData = ImmutableList.of(
                TestGroup.of(0, ImmutableList.of(
                        TestCase.of("sample_1.in", "sample_1.out", ImmutableSet.of(0)),
                        TestCase.of("sample_2.in", "sample_2.out", ImmutableSet.of(0)),
                        TestCase.of("sample_3.in", "sample_3.out", ImmutableSet.of(0))
                )),
                TestGroup.of(-1, ImmutableList.of(
                        TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(-1)),
                        TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(-1)),
                        TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(-1)),
                        TestCase.of("2_2.in", "2_2.out", ImmutableSet.of(-1)),
                        TestCase.of("2_3.in", "2_3.out", ImmutableSet.of(-1))
                ))
        );

        this.config = new FunctionalGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .addSourceFileFieldKeys("encoder", "decoder")
                .build();
        this.engine = new FunctionalGradingEngine();
        this.engine.setGradingLanguage(new PlainCppGradingLanguage());
        this.engine.setScorerLanguage(new PlainCppGradingLanguage());
    }

    @Test
    public void testAC() {
        addSourceFile("encoder", "encoder-AC.cpp");
        addSourceFile("decoder", "decoder-AC.cpp");

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
    public void testCE() {
        addSourceFile("encoder", "encoder-CE.cpp");
        addSourceFile("decoder", "decoder-AC.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_CE);
            assertEquals(result.getScore(), 0);
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testWA80() {
        addSourceFile("encoder", "encoder-AC.cpp");
        addSourceFile("decoder", "decoder-WA.cpp");

        try {
            GradingResult result = runEngine(engine, config);
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 80);
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK90WithCustomScorer() {
        addSourceFile("encoder", "encoder-AC.cpp");
        addSourceFile("decoder", "decoder-AC.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCustomScorer("scorer-nonbinary-OK10-at-1_1.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK);
            assertEquals(result.getScore(), 90);
        } catch (GradingException e) {
            fail();
        }
    }

    private FunctionalGradingConfig createConfigWithCustomScorer(String customScorer) {
        return new FunctionalGradingConfig.Builder().from(config).customScorer(customScorer).build();
    }
}
