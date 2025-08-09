package judgels.michael.problem.programming.grading.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.batch.BatchWithSubtasksGradingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BatchWithSubtasksGradingConfigAdapterTests extends BaseGradingConfigAdapterTests {
    private BatchWithSubtasksGradingConfigAdapter adapter;

    @BeforeEach
    void before() {
        adapter = new BatchWithSubtasksGradingConfigAdapter();
    }

    @Test
    void test_conversion_default_fields() {
        GradingConfigForm form = new GradingConfigForm();
        form.timeLimit = 2000;
        form.memoryLimit = 65536;

        BatchWithSubtasksGradingConfig config = new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of())))
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
                "sample_1.out,sample_2.out,sample_3.out",
                "1_1.out,1_2.out",
                "2_1.out");
        form.sampleTestCaseSubtaskIds = Arrays.asList(
                "2",
                "1,2",
                "2");
        form.testGroupSubtaskIds = Arrays.asList(
                "1,2",
                "2");
        form.subtaskPoints = Arrays.asList(30, 70, null, null);
        form.customScorer = "scorer.cpp";

        BatchWithSubtasksGradingConfig config = new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("sample_1.in", "sample_1.out", ImmutableSet.of(0, 2)),
                                TestCase.of("sample_2.in", "sample_2.out", ImmutableSet.of(0, 1, 2)),
                                TestCase.of("sample_3.in", "sample_3.out", ImmutableSet.of(0, 2)))),
                        TestGroup.of(1, ImmutableList.of(
                                TestCase.of("1_1.in", "1_1.out", ImmutableSet.of(1, 2)),
                                TestCase.of("1_2.in", "1_2.out", ImmutableSet.of(1, 2)))),
                        TestGroup.of(2, ImmutableList.of(
                                TestCase.of("2_1.in", "2_1.out", ImmutableSet.of(2))))))
                .subtaskPoints(ImmutableList.of(30, 70))
                .customScorer("scorer.cpp")
                .build();

        assertThat(adapter.buildConfigFromForm(form)).isEqualTo(config);
        assertThat(adapter.buildConfigFromForm(adapter.buildFormFromConfig(config))).isEqualTo(config);
    }

    @Test
    void test_auto_population() {
        BatchWithSubtasksGradingConfig config = new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .subtaskPoints(ImmutableList.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1_1.in"),
                createFile("hello_1_1.out"),
                createFile("hello_1_2.in"),
                createFile("hello_1_2.out"),
                createFile("hello_2_1.in"),
                createFile("hello_2_1.out"),
                createFile("hello_bogus.txt"));

        BatchWithSubtasksGradingConfig populatedConfig = (BatchWithSubtasksGradingConfig) adapter.autoPopulateTestData(config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new BatchWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", ImmutableSet.of(0)))),
                        TestGroup.of(1, ImmutableList.of(
                                TestCase.of("hello_1_1.in", "hello_1_1.out", ImmutableSet.of(1, 2)),
                                TestCase.of("hello_1_2.in", "hello_1_2.out", ImmutableSet.of(1, 2)))),
                        TestGroup.of(2, ImmutableList.of(
                                TestCase.of("hello_2_1.in", "hello_2_1.out", ImmutableSet.of(2))))))
                .build());
    }

    @Test
    void test_auto_population_single_subtask() {
        BatchWithSubtasksGradingConfig config = new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .subtaskPoints(List.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = List.of(
                createFile("hello_sample.in"),
                createFile("hello_sample.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        BatchWithSubtasksGradingConfig populatedConfig = (BatchWithSubtasksGradingConfig) adapter.autoPopulateTestData(config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new BatchWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("hello_sample.in", "hello_sample.out", Set.of(0, 1)))),
                        TestGroup.of(1, List.of(
                                TestCase.of("hello_1.in", "hello_1.out", Set.of(1)),
                                TestCase.of("hello_2.in", "hello_2.out", Set.of(1))))))
                .subtaskPoints(List.of(100))
                .build());
    }
}
