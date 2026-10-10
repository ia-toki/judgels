package judgels.catalog.problem.programming.grading;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;
import judgels.grading.api.TestCase;
import judgels.grading.api.TestGroup;
import judgels.grading.engines.batch.BatchGradingConfig;
import judgels.grading.engines.batch.BatchWithSubtasksGradingConfig;
import judgels.grading.engines.functional.FunctionalGradingConfig;
import judgels.grading.engines.functional.FunctionalWithSubtasksGradingConfig;
import judgels.grading.engines.interactive.InteractiveGradingConfig;
import judgels.grading.engines.interactive.InteractiveWithSubtasksGradingConfig;
import judgels.grading.engines.outputonly.OutputOnlyGradingConfig;
import judgels.grading.engines.outputonly.OutputOnlyWithSubtasksGradingConfig;
import org.junit.jupiter.api.Test;

public class GradingConfigAutoPopulatorTests {
    @Test
    void batch() {
        BatchGradingConfig config = new BatchGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        GradingConfig populatedConfig = autoPopulate("Batch", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new BatchGradingConfig.Builder()
                .from(config)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("hello_1.in", "hello_1.out", ImmutableSet.of(-1)),
                                TestCase.of("hello_2.in", "hello_2.out", ImmutableSet.of(-1))))))
                .build());
    }

    @Test
    void batch_with_subtasks() {
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

        GradingConfig populatedConfig = autoPopulate("BatchWithSubtasks", config, testDataFiles);
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
    void batch_with_subtasks_single_subtask() {
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

        GradingConfig populatedConfig = autoPopulate("BatchWithSubtasks", config, testDataFiles);
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

    @Test
    void interactive() {
        InteractiveGradingConfig config = new InteractiveGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_1.in"),
                createFile("hello_2.in"));

        GradingConfig populatedConfig = autoPopulate("Interactive", config, testDataFiles);
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

    @Test
    void interactive_with_subtasks() {
        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .subtaskPoints(ImmutableList.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_1_1.in"),
                createFile("hello_1_2.in"),
                createFile("hello_2_1.in"));

        GradingConfig populatedConfig = autoPopulate("InteractiveWithSubtasks", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new InteractiveWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("hello_sample_1.in", "", ImmutableSet.of(0)))),
                        TestGroup.of(1, ImmutableList.of(
                                TestCase.of("hello_1_1.in", "", ImmutableSet.of(1, 2)),
                                TestCase.of("hello_1_2.in", "", ImmutableSet.of(1, 2)))),
                        TestGroup.of(2, ImmutableList.of(
                                TestCase.of("hello_2_1.in", "", ImmutableSet.of(2))))))
                .build());
    }

    @Test
    void interactive_with_subtasks_single_subtask() {
        InteractiveWithSubtasksGradingConfig config = new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .subtaskPoints(List.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = List.of(
                createFile("hello_sample_1.in"),
                createFile("hello_1.in"),
                createFile("hello_2.in"));

        GradingConfig populatedConfig = autoPopulate("InteractiveWithSubtasks", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new InteractiveWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("hello_sample_1.in", "", Set.of(0, 1)))),
                        TestGroup.of(1, List.of(
                                TestCase.of("hello_1.in", "", Set.of(1)),
                                TestCase.of("hello_2.in", "", Set.of(1))))))
                .subtaskPoints(List.of(100))
                .build());
    }

    @Test
    void output_only() {
        OutputOnlyGradingConfig config = new OutputOnlyGradingConfig.Builder().build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        GradingConfig populatedConfig = autoPopulate("OutputOnly", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new OutputOnlyGradingConfig.Builder()
                .from(config)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("hello_1.in", "hello_1.out", ImmutableSet.of(-1)),
                                TestCase.of("hello_2.in", "hello_2.out", ImmutableSet.of(-1))))))
                .build());
    }

    @Test
    void output_only_with_subtasks() {
        OutputOnlyWithSubtasksGradingConfig config = new OutputOnlyWithSubtasksGradingConfig.Builder()
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

        GradingConfig populatedConfig = autoPopulate("OutputOnlyWithSubtasks", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new OutputOnlyWithSubtasksGradingConfig.Builder()
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
    void output_only_with_subtasks_single_subtask() {
        OutputOnlyWithSubtasksGradingConfig config = new OutputOnlyWithSubtasksGradingConfig.Builder()
                .subtaskPoints(List.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = List.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        GradingConfig populatedConfig = autoPopulate("OutputOnlyWithSubtasks", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new OutputOnlyWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", Set.of(0, 1)))),
                        TestGroup.of(1, List.of(
                                TestCase.of("hello_1.in", "hello_1.out", Set.of(1)),
                                TestCase.of("hello_2.in", "hello_2.out", Set.of(1))))))
                .subtaskPoints(List.of(100))
                .build());
    }

    @Test
    void functional() {
        FunctionalGradingConfig config = new FunctionalGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .sourceFileFieldKeys(ImmutableList.of("encoder", "decoder"))
                .build();

        List<FileInfo> testDataFiles = ImmutableList.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        GradingConfig populatedConfig = autoPopulate("Functional", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new FunctionalGradingConfig.Builder()
                .from(config)
                .testData(ImmutableList.of(
                        TestGroup.of(0, ImmutableList.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", ImmutableSet.of(0)))),
                        TestGroup.of(-1, ImmutableList.of(
                                TestCase.of("hello_1.in", "hello_1.out", ImmutableSet.of(-1)),
                                TestCase.of("hello_2.in", "hello_2.out", ImmutableSet.of(-1))))))
                .build());
    }

    @Test
    void functional_with_subtasks() {
        FunctionalWithSubtasksGradingConfig config = new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .sourceFileFieldKeys(ImmutableList.of("encoder", "decoder"))
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

        GradingConfig populatedConfig = autoPopulate("FunctionalWithSubtasks", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new FunctionalWithSubtasksGradingConfig.Builder()
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
    void functional_with_subtasks_single_subtask() {
        FunctionalWithSubtasksGradingConfig config = new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .sourceFileFieldKeys(List.of("encoder", "decoder"))
                .subtaskPoints(List.of(30, 70))
                .build();

        List<FileInfo> testDataFiles = List.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        GradingConfig populatedConfig = autoPopulate("FunctionalWithSubtasks", config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new FunctionalWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", Set.of(0, 1)))),
                        TestGroup.of(1, List.of(
                                TestCase.of("hello_1.in", "hello_1.out", Set.of(1)),
                                TestCase.of("hello_2.in", "hello_2.out", Set.of(1))))))
                .subtaskPoints(List.of(100))
                .build());
    }

    private static GradingConfig autoPopulate(String engine, GradingConfig config, List<FileInfo> testDataFiles) {
        return GradingConfigAutoPopulatorRegistry.getInstance().get(engine).autoPopulateTestData(config, testDataFiles);
    }

    private static FileInfo createFile(String name) {
        return new FileInfo.Builder()
                .name(name)
                .size(10)
                .lastModifiedTime(Instant.now())
                .build();
    }
}
