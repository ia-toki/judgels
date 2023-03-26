package judgels.michael.problem.programming.grading.config;

import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;

public abstract class BaseGradingConfigAdapter implements GradingConfigAdapter {
    protected void fillLimitsFormPartsFromConfig(GradingConfigForm form, GradingConfig config) {
        form.timeLimit = config.getTimeLimit();
        form.memoryLimit = config.getMemoryLimit();
    }

    protected Object[] getLimitsConfigPartsFromForm(GradingConfigForm form) {
        return new Object[]{form.timeLimit, form.memoryLimit};
    }

    protected void fillSourceFileFieldKeysFormPartFromConfig(GradingConfigForm form, List<String> sourceFileFieldKeys) {
        form.sourceFileFieldKeys = String.join(",", sourceFileFieldKeys);
    }

    protected List<String> getSourceFileFieldKeysConfigPartFromForm(GradingConfigForm form) {
        if (form.sourceFileFieldKeys != null) {
            return ImmutableList.copyOf(form.sourceFileFieldKeys.split(","));
        }
        return Collections.emptyList();
    }

    protected void fillTestDataFormPartsFromConfig(GradingConfigForm form, GradingConfig config) {
        form.testCaseInputs = new ArrayList<>();
        form.testCaseOutputs = new ArrayList<>();

        for (TestGroup testGroup : config.getTestData()) {
            form.testCaseInputs.add(testGroup.getTestCases().stream().map(TestCase::getInput).collect(joining(",")));
            form.testCaseOutputs.add(testGroup.getTestCases().stream().map(TestCase::getOutput).collect(joining(",")));
        }
    }

    protected List<TestGroup> getTestDataConfigPartsFromForm(GradingConfigForm form) {
        List<TestGroup> testData = new ArrayList<>();
        for (int i = 0; i < form.testCaseInputs.size(); i++) {
            String[] inputs = form.testCaseInputs.get(i).split(",", -1);
            String[] outputs = form.testCaseOutputs.get(i).split(",", -1);

            List<TestCase> testCases = new ArrayList<>();
            for (int j = 0; j < inputs.length; j++) {
                if (!inputs[j].isEmpty()) {
                    testCases.add(TestCase.of(inputs[j], outputs[j], Collections.emptySet() /* placeholder */));
                }
            }

            testData.add(TestGroup.of(i /* placeholder */, testCases));
        }

        return ImmutableList.copyOf(testData);
    }

    protected List<TestGroup> getTestDataWithoutSubtasksConfigPartsFromForm(GradingConfigForm form) {
        List<TestGroup> testData = getTestDataConfigPartsFromForm(form);
        List<TestGroup> testDataWithoutSubtasks = new ArrayList<>();

        for (TestGroup testGroup : testData) {
            int subtaskId = testGroup.getId() == 0 ? 0 : -1;
            testDataWithoutSubtasks.add(new TestGroup.Builder()
                    .from(testGroup)
                    .id(subtaskId)
                    .testCases(Lists.transform(testGroup.getTestCases(), tc ->
                            new TestCase.Builder()
                                    .from(tc)
                                    .subtaskIds(Collections.singleton(subtaskId))
                                    .build()))
                    .build());
        }

        return ImmutableList.copyOf(testDataWithoutSubtasks);
    }

    protected void fillCustomScorerFormPartFromConfig(GradingConfigForm form, Optional<String> customScorer) {
        form.customScorer = customScorer.orElse("(none)");
    }

    protected Optional<String> getCustomScorerConfigPartFromForm(GradingConfigForm form) {
        if (!form.customScorer.equals("(none)")) {
            return Optional.of(form.customScorer);
        }
        return Optional.empty();
    }

    protected void fillCommunicatorFormPartFromConfig(GradingConfigForm form, Optional<String> communicator) {
        form.communicator = communicator.orElse("(none)");
    }

    protected Optional<String> getCommunicatorConfigPartFromForm(GradingConfigForm form) {
        if (!form.communicator.equals("(none)")) {
            return Optional.of(form.communicator);
        }
        return Optional.empty();
    }
}
