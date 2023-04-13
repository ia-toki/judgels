package judgels.gabriel.engines.interactive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.AbstractGradingConfigTests;
import org.junit.jupiter.api.Test;

class InteractiveGradingConfigTests extends AbstractGradingConfigTests {
    @Test
    void config_deserializes_and_serializes() throws IOException {
        InteractiveGradingConfig config = new InteractiveGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .addTestData(
                        TestGroup.of(0, ImmutableList.of(TestCase.of("tc1.in", "", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("tc2.in", "", ImmutableSet.of(-1)),
                                TestCase.of("tc3.in", "", ImmutableSet.of(-1)))))
                .communicator("helper.cpp")
                .build();

        assertConfig("interactive", InteractiveGradingConfig.class, config);
    }
}
