package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.outputonly.OutputOnlyWithSubtasksGradingConfig;

public class OutputOnlyWithSubtasksGradingConfigAdapter extends BaseGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillTestDataWithSubtasksFormPartsFromConfig(form, config);

        OutputOnlyWithSubtasksGradingConfig castConfig = (OutputOnlyWithSubtasksGradingConfig) config;
        fillCustomScorerFormPartFromConfig(form, castConfig.getCustomScorer());

        return form;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] testDataParts = getTestDataWithSubtasksConfigPartsFromForm(form);
        Optional<String> customScorerPart = getCustomScorerConfigPartFromForm(form);

        return new OutputOnlyWithSubtasksGradingConfig.Builder()
                .testData((List<TestGroup>) testDataParts[0])
                .subtaskPoints((List<Integer>) testDataParts[1])
                .customScorer(customScorerPart)
                .build();
    }
}
