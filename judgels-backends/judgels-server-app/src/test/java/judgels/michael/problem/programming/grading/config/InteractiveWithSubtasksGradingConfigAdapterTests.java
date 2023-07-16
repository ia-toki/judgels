package judgels.michael.problem.programming.grading.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveWithSubtasksGradingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InteractiveWithSubtasksGradingConfigAdapterTests extends BaseGradingConfigAdapterTests {
    private InteractiveWithSubtasksGradingConfigAdapter adapter;

    @BeforeEach
    void before() {
        adapter = new InteractiveWithSubtasksGradingConfigAdapter();
    }

    @Test
    void test_conversion_default_fields() {
        GradingConfigForm form = new GradingConfigForm();
        form.timeLimit = 2000;
        form.memoryLimit = 65536;

        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .testData(List.of(
                        TestGroup.of(0, List.of())))
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
                "sample_1.in,sample_2.in,sample_3.in",
                "1_1.in,1_2.in",
                "2_1.in");
        form.testCaseOutputs = Arrays.asList(
                ",,",
                ",,",
                "");
        form.sampleTestCaseSubtaskIds = Arrays.asList(
                "2",
                "1,2",
                "2");
        form.testGroupSubtaskIds = Arrays.asList(
                "1,2",
                "2");
        form.subtaskPoints = Arrays.asList(30, 70, null, null);
        form.communicator = "communicator.cpp";

        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("sample_1.in", "", Set.of(0, 2)),
                                TestCase.of("sample_2.in", "", Set.of(0, 1, 2)),
                                TestCase.of("sample_3.in", "", Set.of(0, 2)))),
                        TestGroup.of(1, List.of(
                                TestCase.of("1_1.in", "", Set.of(1, 2)),
                                TestCase.of("1_2.in", "", Set.of(1, 2)))),
                        TestGroup.of(2, List.of(
                                TestCase.of("2_1.in", "", Set.of(2))))))
                .subtaskPoints(List.of(30, 70))
                .communicator("communicator.cpp")
                .build();

        assertThat(adapter.buildConfigFromForm(form)).isEqualTo(config);
        assertThat(adapter.buildConfigFromForm(adapter.buildFormFromConfig(config))).isEqualTo(config);
    }

    @Test
    void test_auto_population() {
        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .subtaskPoints(List.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = List.of(
                createFile("hello_sample_1.in"),
                createFile("hello_1_1.in"),
                createFile("hello_1_2.in"),
                createFile("hello_2_1.in"));

        InteractiveWithSubtasksGradingConfig populatedConfig = (InteractiveWithSubtasksGradingConfig) adapter.autoPopulateTestData(config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new InteractiveWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("hello_sample_1.in", "", Set.of(0)))),
                        TestGroup.of(1, List.of(
                                TestCase.of("hello_1_1.in", "", Set.of(1, 2)),
                                TestCase.of("hello_1_2.in", "", Set.of(1, 2)))),
                        TestGroup.of(2, List.of(
                                TestCase.of("hello_2_1.in", "", Set.of(2))))))
                .build());
    }
}
