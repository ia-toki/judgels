package judgels.grading.engines.functional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import judgels.grading.api.TestCase;
import judgels.grading.api.TestGroup;
import judgels.grading.engines.AbstractGradingConfigTests;
import org.junit.jupiter.api.Test;

class FunctionalGradingConfigTests extends AbstractGradingConfigTests {
    @Test
    void config_deserializes_and_serializes() throws IOException {
        FunctionalGradingConfig config = new FunctionalGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .addTestData(
                        TestGroup.of(0, ImmutableList.of(TestCase.of("tc1.in", "tc1.out", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("tc2.in", "tc2.out", ImmutableSet.of(-1)),
                                TestCase.of("tc3.in", "tc3.out", ImmutableSet.of(-1)))))
                .addSourceFileFieldKeys("encoder", "decoder")
                .build();

        assertConfig("functional", FunctionalGradingConfig.class, config);
    }
}
