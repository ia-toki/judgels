package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveGradingConfig;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class InteractiveGradingEngineTest extends BlackBoxGradingEngineTest {

    private final InteractiveGradingEngine engine;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;

    public InteractiveGradingEngineTest() {
        super("interactive");

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

        this.engine = new InteractiveGradingEngine();
        this.engine.setCommunicatorLanguage(new PlainCppGradingLanguage());
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
                            new SubtaskFinalResult(-1, VERDICT_AC, 100.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK40() {
        addSourceFile("source", "linsearch-WA-at-subtask_2.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-binary.cpp"));
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 40);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_WA, 40.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK90() {
        addSourceFile("source", "binsearch-OK.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithCommunicator("communicator-nonbinary-OK10-at-1_1.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK);
            assertEquals(result.getScore(), 90);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_OK, 90.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    private InteractiveGradingConfig createConfigWithCommunicator(String communicator) {
        return new InteractiveGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .communicator(communicator)
                .build();
    }
}
