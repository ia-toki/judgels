package judgels.michael.problem.programming.grading.config;

import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;

public abstract class BaseGradingConfigAdapter implements GradingConfigAdapter {
    protected void fillFormPartsFromConfig(GradingConfigForm form, GradingConfig config) {
        form.timeLimit = config.getTimeLimit();
        form.memoryLimit = config.getMemoryLimit();

        form.testCaseInputs = new ArrayList<>();
        form.testCaseOutputs = new ArrayList<>();

        for (TestGroup testGroup : config.getTestData()) {
            form.testCaseInputs.add(testGroup.getTestCases().stream().map(TestCase::getInput).collect(joining(",")));
            form.testCaseOutputs.add(testGroup.getTestCases().stream().map(TestCase::getOutput).collect(joining(",")));
        }
    }

    protected Object[] getConfigPartsFromForm(GradingConfigForm form) {
        int timeLimit = form.timeLimit;
        int memoryLimit = form.memoryLimit;

        List<TestGroup> testData = new ArrayList<>();
        for (int i = 0; i < form.testCaseInputs.size(); i++) {
            String[] inputs = form.testCaseInputs.get(i).split(",", -1);
            String[] outputs = form.testCaseOutputs.get(i).split(",", -1);

            List<TestCase> testCases = new ArrayList<>();
            for (int j = 0; j < inputs.length; j++) {
                if (!inputs[j].isEmpty() && !outputs[j].isEmpty()) {
                    testCases.add(TestCase.of(inputs[j], outputs[j], Collections.emptySet() /* placeholder */));
                }
            }

            testData.add(TestGroup.of(i /* placeholder */, testCases));
        }

        return new Object[]{timeLimit, memoryLimit, ImmutableList.copyOf(testData)};
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
}
