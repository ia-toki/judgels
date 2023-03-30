package judgels.michael.problem.programming.grading.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import judgels.fs.FileInfo;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveGradingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InteractiveGradingConfigAdapterTests extends BaseGradingConfigAdapterTests {
    private InteractiveGradingConfigAdapter adapter;

    @BeforeEach
    void before() {
        adapter = new InteractiveGradingConfigAdapter();
    }

    @Test
    void test_conversion_default_fields() {
        GradingConfigForm form = new GradingConfigForm();
        form.timeLimit = 2000;
        form.memoryLimit = 65536;

        InteractiveGradingConfig config = new InteractiveGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of()),
                        TestGroup.of(-1, ImmutableList.of())))
                .build();

        assertThat(adapter.buildConfigFromForm(form)).isEqualTo(config);
        assertThat(adapter.buildConfigFromForm(adapter.buildFormFromConfig(config))).isEqualTo(config);
    }

    @Test
    void test_conversion_with_fields() {
        GradingConfigForm form = new GradingConfigForm();
        form.timeLimit = 2000;
        form.memoryLimit = 65536;
        form.testCaseInputs = Arrays.asList(
                "sample_1.in",
                "1.in,2.in");
        form.testCaseOutputs = Arrays.asList(
                "",
                ",");
        form.communicator = "communicator.cpp";

        InteractiveGradingConfig config = new InteractiveGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("sample_1.in", "", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("1.in", "", ImmutableSet.of(-1)),
                                TestCase.of("2.in", "", ImmutableSet.of(-1))))))
                .communicator("communicator.cpp")
                .build();

        assertThat(adapter.buildConfigFromForm(form)).isEqualTo(config);
        assertThat(adapter.buildConfigFromForm(adapter.buildFormFromConfig(config))).isEqualTo(config);
    }

    @Test
    void test_auto_population() {
        InteractiveGradingConfig config = new InteractiveGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_1.in"),
                createFile("hello_2.in"));

        InteractiveGradingConfig populatedConfig = (InteractiveGradingConfig) adapter.autoPopulateTestData(config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new InteractiveGradingConfig.Builder()
                .from(config)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("hello_sample_1.in", "", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("hello_1.in", "", ImmutableSet.of(-1)),
                                TestCase.of("hello_2.in", "", ImmutableSet.of(-1))))))
                .build());
    }
}
