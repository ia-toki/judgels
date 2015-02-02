package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SampleTestCase;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.SubtaskResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public final class InteractiveWithSubtasksGraderTest extends BlackBoxGraderTest {

    private final InteractiveWithSubtasksGrader grader;
    private final InteractiveWithSubtasksGradingConfig config;

    public InteractiveWithSubtasksGraderTest() {
        super("blackbox/interactive");

        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig();

        config.timeLimitInMilliseconds = 1000;
        config.memoryLimitInKilobytes = 65536;
        config.sampleTestData = ImmutableList.of(
                new SampleTestCase("sample_1.in", null, ImmutableSet.of(0)),
                new SampleTestCase("sample_2.in", null, ImmutableSet.of(0)),
                new SampleTestCase("sample_3.in", null, ImmutableSet.of(1))
        );

        config.testData = ImmutableList.of(
                new TestGroup(ImmutableList.of(
                        new TestCase("1_1.in", null),
                        new TestCase("1_2.in", null)
                ), ImmutableSet.of(0, 1)),

                new TestGroup(ImmutableList.of(
                        new TestCase("2_1.in", null),
                        new TestCase("2_2.in", null),
                        new TestCase("2_3.in", null)
                ), ImmutableSet.of(1))
        );

        config.subtaskPoints = ImmutableList.of(30, 70);
        config.communicator = "communicator.cpp";

        this.config = config;
        this.grader = new InteractiveWithSubtasksGrader();
    }

    @Test
    public void testOK() {
        addSourceFile("source", "binsearch-OK.cpp");

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
}
