package judgels.gabriel.engines.batch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.AbstractGradingConfigTests;
import org.junit.jupiter.api.Test;

class BatchGradingConfigTests extends AbstractGradingConfigTests {
    @Test
    void config_deserializes_and_serializes() throws IOException {
        BatchGradingConfig config = new BatchGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .addTestData(
                        TestGroup.of(0, ImmutableList.of(TestCase.of("tc1.in", "tc1.out", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("tc2.in", "tc2.out", ImmutableSet.of(-1)),
                                TestCase.of("tc3.in", "tc3.out", ImmutableSet.of(-1)))))
                .build();

        assertConfig("batch", BatchGradingConfig.class, config);
    }
}
