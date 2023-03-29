package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalWithSubtasksGradingConfig;

public class FunctionalWithSubtasksGradingConfigAdapter extends BaseGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillLimitsFormPartsFromConfig(form, config);
        fillTestDataWithSubtasksFormPartsFromConfig(form, config);

        FunctionalWithSubtasksGradingConfig castConfig = (FunctionalWithSubtasksGradingConfig) config;
        fillSourceFileFieldKeysFormPartFromConfig(form, castConfig.getSourceFileFieldKeys());
        fillCustomScorerFormPartFromConfig(form, castConfig.getCustomScorer());

        return form;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] limitParts = getLimitsConfigPartsFromForm(form);
        List<String> sourceFileFieldKeysPart = getSourceFileFieldKeysConfigPartFromForm(form);
        Object[] testDataParts = getTestDataWithSubtasksConfigPartsFromForm(form);
        Optional<String> customScorerPart = getCustomScorerConfigPartFromForm(form);

        return new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit((int) limitParts[0])
                .memoryLimit((int) limitParts[1])
                .sourceFileFieldKeys(sourceFileFieldKeysPart)
                .testData((List<TestGroup>) testDataParts[0])
                .subtaskPoints((List<Integer>) testDataParts[1])
                .customScorer(customScorerPart)
                .build();
    }
}
