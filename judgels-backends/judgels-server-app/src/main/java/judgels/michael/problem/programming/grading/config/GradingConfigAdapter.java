package judgels.michael.problem.programming.grading.config;

import judgels.grading.api.GradingConfig;

public interface GradingConfigAdapter {
    GradingConfigForm buildFormFromConfig(GradingConfig config);
    GradingConfig buildConfigFromForm(GradingConfigForm form);
}
