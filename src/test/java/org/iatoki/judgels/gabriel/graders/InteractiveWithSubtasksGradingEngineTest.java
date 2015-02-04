package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.InteractiveWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.engines.InteractiveWithSubtasksGradingEngine;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public final class InteractiveWithSubtasksGradingEngineTest extends BlackBoxGraderTest {

    private final InteractiveWithSubtasksGradingEngine grader;
    private final InteractiveWithSubtasksGradingConfig config;

    public InteractiveWithSubtasksGradingEngineTest() {
        super("blackbox/interactive");

        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig();

        config.timeLimitInMilliseconds = 1000;
        config.memoryLimitInKilobytes = 65536;
        config.testData = ImmutableList.of(
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

        config.subtaskPoints = ImmutableList.of(30, 70);
        config.communicator = "communicator.cpp";

        this.config = config;
        this.grader = new InteractiveWithSubtasksGradingEngine();
    }

    @Test
    public void testOK() {
        addSourceFile("source", "binsearch-OK.cpp");

        try {
            BlackBoxGradingResult result = runGrader(grader, config);
            assertEquals(result.getVerdict(), makeConcrete(ScoringVerdict.OK));
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = result.getDetails();
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(makeConcrete(ScoringVerdict.ACCEPTED), 30.0),
                            new SubtaskFinalResult(makeConcrete(ScoringVerdict.ACCEPTED), 70.0))
            );
        } catch (GradingException e) {
            fail();
        }
    }
}
