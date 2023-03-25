package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveGradingConfig;

public class InteractiveGradingConfigAdapter extends SingleSourceFileWithoutSubtasksGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillFormPartsFromConfig(form, config);

        InteractiveGradingConfig castConfig = (InteractiveGradingConfig) config;
        fillCommunicatorFormPartFromConfig(form, castConfig.getCommunicator());

        return form;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] parts = getSingleSourceFileWithoutSubtasksConfigPartsFromForm(form);
        Optional<String> communicatorPart = getCommunicatorConfigPartFromForm(form);

        return new InteractiveGradingConfig.Builder()
                .timeLimit((int) parts[0])
                .memoryLimit((int) parts[1])
                .testData((List<TestGroup>) parts[2])
                .communicator(communicatorPart)
                .build();
    }
}
