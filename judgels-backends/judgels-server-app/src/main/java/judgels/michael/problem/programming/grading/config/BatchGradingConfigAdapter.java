package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.batch.BatchGradingConfig;

public class BatchGradingConfigAdapter extends BaseGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillLimitsFormPartsFromConfig(form, config);
        fillTestDataWithoutSubtasksFormPartsFromConfig(form, config);

        BatchGradingConfig castConfig = (BatchGradingConfig) config;
        fillCustomScorerFormPartFromConfig(form, castConfig.getCustomScorer());

        return form;
    }

    @Override
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] limitParts = getLimitsConfigPartsFromForm(form);
        List<TestGroup> testDataPart = getTestDataWithoutSubtasksConfigPartsFromForm(form);
        Optional<String> customScorerPart = getCustomScorerConfigPartFromForm(form);

        return new BatchGradingConfig.Builder()
                .timeLimit((int) limitParts[0])
                .memoryLimit((int) limitParts[1])
                .testData(testDataPart)
                .customScorer(customScorerPart)
                .build();
    }

    @Override
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        return new BatchGradingConfig.Builder()
                .from(config)
                .testData(autoPopulateTestDataByFilename(testDataFiles))
                .build();
    }
}
