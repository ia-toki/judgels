package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.SingleSourceFileGradingConfig;

public abstract class SingleSourceFileWithoutSubtasksBlackBoxGradingEngineAdapter extends SingleSourceFileBlackBoxGradingEngineAdapter {
    protected final void fillSingleSourceFileWithoutSubtasksBlackBoxGradingConfigFormPartsFromConfig(SingleSourceFileWithoutSubtasksBlackBoxGradingConfigForm form, SingleSourceFileGradingConfig config) {
        fillSingleSourceFileBlackBoxGradingConfigFormPartsFromConfig(form, config);
    }

    protected final List<Object> createSingleSourceFileWithoutSubtasksBlackBoxGradingConfigPartsFromForm(SingleSourceFileWithoutSubtasksBlackBoxGradingConfigForm form) {
        List<Object> parts = createSingleSourceFileBlackBoxGradingConfigPartsFromForm(form);

        int timeLimit = (int) parts.get(0);
        int memoryLimit = (int) parts.get(1);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts.get(2);

        ImmutableList.Builder<TestGroup> filledTestData = ImmutableList.builder();

        for (int i = 0; i < testData.size(); i++) {
            TestGroup testGroup = testData.get(i);

            if (testGroup.getId() == 0) {
                ImmutableList.Builder<TestCase> sampleTestCases = ImmutableList.builder();

                for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                    TestCase sampleTestCase = testGroup.getTestCases().get(j);
                    sampleTestCases.add(TestCase.of(sampleTestCase.getInput(), sampleTestCase.getOutput(), ImmutableSet.of(0)));
                }

                filledTestData.add(TestGroup.of(0, sampleTestCases.build()));
            } else {
                filledTestData.add(TestGroup.of(-1, Lists.transform(testData.get(i).getTestCases(), tc -> TestCase.of(tc.getInput(), tc.getOutput(), ImmutableSet.of(-1)))));
            }
        }

        return ImmutableList.of(timeLimit, memoryLimit, filledTestData.build());
    }
}
