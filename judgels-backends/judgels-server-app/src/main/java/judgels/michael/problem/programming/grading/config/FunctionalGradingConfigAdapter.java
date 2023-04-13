package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalGradingConfig;

public class FunctionalGradingConfigAdapter extends BaseGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillLimitsFormPartsFromConfig(form, config);
        fillTestDataWithoutSubtasksFormPartsFromConfig(form, config);

        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;
        fillSourceFileFieldKeysFormPartFromConfig(form, castConfig.getSourceFileFieldKeys());
        fillCustomScorerFormPartFromConfig(form, castConfig.getCustomScorer());

        return form;
    }

    @Override
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] limitParts = getLimitsConfigPartsFromForm(form);
        List<String> sourceFileFieldKeysPart = getSourceFileFieldKeysConfigPartFromForm(form);
        List<TestGroup> testDataPart = getTestDataWithoutSubtasksConfigPartsFromForm(form);
        Optional<String> customScorerPart = getCustomScorerConfigPartFromForm(form);

        return new FunctionalGradingConfig.Builder()
                .timeLimit((int) limitParts[0])
                .memoryLimit((int) limitParts[1])
                .sourceFileFieldKeys(sourceFileFieldKeysPart)
                .testData(testDataPart)
                .customScorer(customScorerPart)
                .build();
    }

    @Override
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        return new FunctionalGradingConfig.Builder()
                .from(config)
                .testData(autoPopulateTestDataByFilename(testDataFiles))
                .build();
    }
}
