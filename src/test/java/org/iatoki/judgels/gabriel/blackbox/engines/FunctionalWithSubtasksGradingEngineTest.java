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
import org.iatoki.judgels.gabriel.blackbox.configs.FunctionalWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public final class FunctionalWithSubtasksGradingEngineTest extends BlackBoxGradingEngineTest {
    private final FunctionalWithSubtasksGradingEngine engine;
    private final FunctionalWithSubtasksGradingConfig config;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;
    private final List<Integer> subtaskPoints;

    public FunctionalWithSubtasksGradingEngineTest() {
        super("functional");

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

        this.config = new FunctionalWithSubtasksGradingConfig(timeLimit, memoryLimit, testData, ImmutableList.of("encoder", "decoder"), subtaskPoints);
        this.engine = new FunctionalWithSubtasksGradingEngine();
        this.engine.setGradingLanguage(new PlainCppGradingLanguage());
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
                    new SubtaskFinalResult(1, VERDICT_AC, 30.0),
                    new SubtaskFinalResult(2, VERDICT_AC, 70.0))
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
    public void testOK30() {
        addSourceFile("encoder", "encoder-AC.cpp");
        addSourceFile("decoder", "decoder-WA.cpp");

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
}
