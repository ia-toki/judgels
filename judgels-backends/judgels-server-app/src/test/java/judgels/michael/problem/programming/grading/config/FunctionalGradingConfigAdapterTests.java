package judgels.michael.problem.programming.grading.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalGradingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FunctionalGradingConfigAdapterTests extends BaseGradingConfigAdapterTests {
    private FunctionalGradingConfigAdapter adapter;

    @BeforeEach
    void before() {
        adapter = new FunctionalGradingConfigAdapter();
    }

    @Test
    void test_conversion_default_fields() {
        GradingConfigForm form = new GradingConfigForm();
        form.timeLimit = 2000;
        form.memoryLimit = 65536;
        form.sourceFileFieldKeys = "encoder,decoder";

        FunctionalGradingConfig config = new FunctionalGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .sourceFileFieldKeys(List.of("encoder", "decoder"))
                .testData(List.of(
                        TestGroup.of(0, List.of()),
                        TestGroup.of(-1, List.of())))
                .build();

        assertThat(adapter.buildConfigFromForm(form)).isEqualTo(config);
        assertThat(adapter.buildConfigFromForm(adapter.buildFormFromConfig(config))).isEqualTo(config);
    }

    @Test
    void test_conversion_with_fields() {
        GradingConfigForm form = new GradingConfigForm();
        form.timeLimit = 2000;
        form.memoryLimit = 65536;
        form.sourceFileFieldKeys = "encoder,decoder";
        form.testCaseInputs = Arrays.asList(
                "sample_1.in",
                "1.in,2.in");
        form.testCaseOutputs = Arrays.asList(
                "sample_1.out",
                "1.out,2.out");
        form.customScorer = "scorer.cpp";

        FunctionalGradingConfig config = new FunctionalGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .sourceFileFieldKeys(List.of("encoder", "decoder"))
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("sample_1.in", "sample_1.out", Set.of(0)))),
                        TestGroup.of(-1, List.of(
                                TestCase.of("1.in", "1.out", Set.of(-1)),
                                TestCase.of("2.in", "2.out", Set.of(-1))))))
                .customScorer("scorer.cpp")
                .build();

        assertThat(adapter.buildConfigFromForm(form)).isEqualTo(config);
        assertThat(adapter.buildConfigFromForm(adapter.buildFormFromConfig(config))).isEqualTo(config);
    }

    @Test
    void test_auto_population() {
        FunctionalGradingConfig config = new FunctionalGradingConfig.Builder()
                .timeLimit(2000)
                .memoryLimit(65536)
                .sourceFileFieldKeys(List.of("encoder", "decoder"))
                .build();

        List<FileInfo> testDataFiles = List.of(
                createFile("hello_sample_1.in"),
                createFile("hello_sample_1.out"),
                createFile("hello_1.in"),
                createFile("hello_1.out"),
                createFile("hello_2.in"),
                createFile("hello_2.out"),
                createFile("hello_bogus.txt"));

        FunctionalGradingConfig populatedConfig = (FunctionalGradingConfig) adapter.autoPopulateTestData(config, testDataFiles);
        assertThat(populatedConfig).isEqualTo(new FunctionalGradingConfig.Builder()
                .from(config)
                .testData(List.of(
                        TestGroup.of(0, List.of(
                                TestCase.of("hello_sample_1.in", "hello_sample_1.out", Set.of(0)))),
                        TestGroup.of(-1, List.of(
                                TestCase.of("hello_1.in", "hello_1.out", Set.of(-1)),
                                TestCase.of("hello_2.in", "hello_2.out", Set.of(-1))))))
                .build());
    }
}
