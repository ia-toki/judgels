package judgels.michael.problem.programming.grading.config;

import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveGradingConfig;

public class InteractiveGradingConfigAdapter extends BaseGradingConfigAdapter {
    @Override
    public GradingConfigForm buildFormFromConfig(GradingConfig config) {
        GradingConfigForm form = new GradingConfigForm();
        fillLimitsFormPartsFromConfig(form, config);
        fillTestDataFormPartsFromConfig(form, config);

        InteractiveGradingConfig castConfig = (InteractiveGradingConfig) config;
        fillCommunicatorFormPartFromConfig(form, castConfig.getCommunicator());

        return form;
    }

    @Override
    public GradingConfig buildConfigFromForm(GradingConfigForm form) {
        Object[] limitParts = getLimitsConfigPartsFromForm(form);
        List<TestGroup> testDataPart = getTestDataWithoutSubtasksConfigPartsFromForm(form);
        Optional<String> communicatorPart = getCommunicatorConfigPartFromForm(form);

        return new InteractiveGradingConfig.Builder()
                .timeLimit((int) limitParts[0])
                .memoryLimit((int) limitParts[1])
                .testData(testDataPart)
                .communicator(communicatorPart)
                .build();
    }
}
