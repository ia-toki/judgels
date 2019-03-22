package judgels.gabriel.engines.functional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.AbstractGradingConfigTests;
import org.junit.jupiter.api.Test;

class FunctionalWithSubtasksGradingConfigTests extends AbstractGradingConfigTests {
    @Test
    void config_deserializes_and_serializes() throws IOException {
        FunctionalWithSubtasksGradingConfig config = new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .addTestData(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("tc1.in", "tc1.out", ImmutableSet.of(0, 1, 2)))),
                        TestGroup.of(1, ImmutableList.of(
                                TestCase.of("tc1.in", "tc1.out", ImmutableSet.of(1, 2)))),
                        TestGroup.of(2, ImmutableList.of(
                                TestCase.of("tc2.in", "tc2.out", ImmutableSet.of(2)),
                                TestCase.of("tc3.in", "tc3.out", ImmutableSet.of(2)))))
                .addSourceFileFieldKeys("encoder", "decoder")
                .addSubtaskPoints(30, 70)
                .customScorer("helper.cpp")
                .build();

        assertConfig("functional-with-subtasks", FunctionalWithSubtasksGradingConfig.class, config);
    }
}
