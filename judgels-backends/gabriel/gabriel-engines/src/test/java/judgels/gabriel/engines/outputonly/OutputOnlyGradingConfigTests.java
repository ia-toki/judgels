package judgels.gabriel.engines.outputonly;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.AbstractGradingConfigTests;
import org.junit.jupiter.api.Test;

class OutputOnlyGradingConfigTests extends AbstractGradingConfigTests {
    @Test
    void config_deserializes_and_serializes() throws IOException {
        OutputOnlyGradingConfig config = new OutputOnlyGradingConfig.Builder()
                .addTestData(
                        TestGroup.of(0, ImmutableList.of(TestCase.of("tc1.in", "tc1.out", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("tc2.in", "tc2.out", ImmutableSet.of(-1)),
                                TestCase.of("tc3.in", "tc3.out", ImmutableSet.of(-1)))))
                .build();

        assertConfig("outputonly", OutputOnlyGradingConfig.class, config);
    }
}
