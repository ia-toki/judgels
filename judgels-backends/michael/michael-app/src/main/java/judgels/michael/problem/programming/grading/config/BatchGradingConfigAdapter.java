package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.batch.BatchGradingConfig;

public class BatchGradingConfigAdapter extends SingleSourceFileWithoutSubtasksGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillFormPartsFromConfig(form, config);

        BatchGradingConfig castConfig = (BatchGradingConfig) config;
        fillCustomScorerFormPartFromConfig(form, castConfig.getCustomScorer());

        return form;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] parts = getSingleSourceFileWithoutSubtasksConfigPartsFromForm(form);
        Optional<String> customScorerPart = getCustomScorerConfigPartFromForm(form);

        return new BatchGradingConfig.Builder()
                .timeLimit((int) parts[0])
                .memoryLimit((int) parts[1])
                .testData((List<TestGroup>) parts[2])
                .customScorer(customScorerPart)
                .build();
    }
}
