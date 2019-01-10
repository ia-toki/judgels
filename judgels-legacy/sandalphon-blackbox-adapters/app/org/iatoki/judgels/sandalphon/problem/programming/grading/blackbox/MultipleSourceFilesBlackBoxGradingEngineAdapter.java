package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.MultipleSourceFilesBlackBoxGradingConfig;

import java.util.List;

public abstract class MultipleSourceFilesBlackBoxGradingEngineAdapter extends AbstractBoxGradingEngineAdapter {
    protected final void fillMultipleSourceFileBlackBoxGradingConfigFormPartsFromConfig(MultipleSourceFilesBlackBoxGradingConfigForm form, MultipleSourceFilesBlackBoxGradingConfig config) {
        fillAbstractBlackBoxGradingFormPartsFromConfig(form, config);

        form.sourceFileFieldKeys = Joiner.on(",").join(config.getSourceFileFieldKeys());
    }

    protected final List<Object> createMultipleSourceFileBlackBoxGradingConfigPartsFromForm(MultipleSourceFilesBlackBoxGradingConfigForm form) {
        List<Object> parts = createAbstractBlackBoxGradingConfigPartsFromForm(form);

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
                    sampleTestCases.add(new TestCase(sampleTestCase.getInput(), sampleTestCase.getOutput(), ImmutableSet.of(0)));
                }

                filledTestData.add(new TestGroup(0, sampleTestCases.build()));
            } else {
                filledTestData.add(new TestGroup(-1, Lists.transform(testData.get(i).getTestCases(), tc -> new TestCase(tc.getInput(), tc.getOutput(), ImmutableSet.of(-1)))));
            }
        }

        List<String> sourceFileFieldKeys;
        if (form.sourceFileFieldKeys != null) {
            sourceFileFieldKeys = ImmutableList.copyOf(form.sourceFileFieldKeys.split(","));
        } else {
            sourceFileFieldKeys = ImmutableList.of();
        }

        return ImmutableList.of(timeLimit, memoryLimit, filledTestData.build(), sourceFileFieldKeys);
    }
}
