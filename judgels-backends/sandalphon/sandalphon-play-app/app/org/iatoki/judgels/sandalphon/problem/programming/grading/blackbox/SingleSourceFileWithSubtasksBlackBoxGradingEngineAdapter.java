package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.gabriel.api.Subtask;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.SingleSourceFileWithSubtasksGradingConfig;

public abstract class SingleSourceFileWithSubtasksBlackBoxGradingEngineAdapter extends SingleSourceFileBlackBoxGradingEngineAdapter {
    protected final void fillSingleSourceFileWithSubtasksBlackBoxGradingConfigFormPartsFromConfig(SingleSourceFileWithSubtasksBlackBoxGradingConfigForm form, SingleSourceFileWithSubtasksGradingConfig config) {
        fillSingleSourceFileBlackBoxGradingConfigFormPartsFromConfig(form, config);

        int subtasksCount = Math.max(10, config.getSubtasks().size());

        ImmutableList.Builder<List<Integer>> testGroupSubtaskIds = ImmutableList.builder();

        for (TestGroup testGroup : config.getTestData()) {
            if (testGroup.getId() == 0) {
                ImmutableList.Builder<List<Integer>> sampleTestCaseSubtaskIds = ImmutableList.builder();
                for (TestCase testCase : testGroup.getTestCases()) {
                    sampleTestCaseSubtaskIds.add(getSubtaskIds(testCase, subtasksCount));
                }
                form.sampleTestCaseSubtaskIds = sampleTestCaseSubtaskIds.build();
            } else {
                if (testGroup.getTestCases().isEmpty()) {
                    // actually should never happen because a test group must contain at least one test case; just in case
                    testGroupSubtaskIds.add(ImmutableList.of());
                } else {
                    testGroupSubtaskIds.add(getSubtaskIds(testGroup.getTestCases().get(0), subtasksCount));
                }
            }
        }

        form.testGroupSubtaskIds = testGroupSubtaskIds.build();
        List<Integer> subtaskPoints = Lists.newArrayList();
        for (Subtask subtask : config.getSubtasks()) {
            subtaskPoints.add(subtask.getPoints());
        }
        for (int i = config.getSubtasks().size(); i < subtasksCount; i++) {
            subtaskPoints.add(null);
        }

        form.subtaskPoints = subtaskPoints;
    }

    protected final List<Object> createSingleSourceFileWithSubtasksBlackBoxGradingConfigPartsFromForm(SingleSourceFileWithSubtasksBlackBoxGradingConfigForm form) {
        List<Object> parts = createSingleSourceFileBlackBoxGradingConfigPartsFromForm(form);

        int timeLimit = (int) parts.get(0);
        int memoryLimit = (int) parts.get(1);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts.get(2);

        int subtasksCount = 0;

        ImmutableList.Builder<TestGroup> filledTestData = ImmutableList.builder();

        for (int i = 0; i < testData.size(); i++) {
            TestGroup testGroup = testData.get(i);

            if (testGroup.getId() == 0) {
                ImmutableList.Builder<TestCase> sampleTestCases = ImmutableList.builder();

                for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                    TestCase sampleTestCase = testGroup.getTestCases().get(j);

                    List<Integer> formSubtaskIds;

                    if (form.sampleTestCaseSubtaskIds == null || j >= form.sampleTestCaseSubtaskIds.size()) {
                        formSubtaskIds = ImmutableList.of();
                    } else {
                        formSubtaskIds = form.sampleTestCaseSubtaskIds.get(j);
                    }

                    Set<Integer> subtaskIds = formSubtaskIds.stream()
                            .filter(s -> s != null)
                            .collect(Collectors.toSet());

                    subtaskIds.add(0);

                    sampleTestCases.add(TestCase.of(sampleTestCase.getInput(), sampleTestCase.getOutput(), subtaskIds));

                    if (!subtaskIds.isEmpty()) {
                        subtasksCount = Math.max(subtasksCount, Collections.max(subtaskIds));
                    }
                }

                filledTestData.add(TestGroup.of(0, sampleTestCases.build()));
            } else {
                List<Integer> formSubtaskIds;

                if (form.testGroupSubtaskIds == null || i - 1 >= form.testGroupSubtaskIds.size()) {
                    formSubtaskIds = ImmutableList.of();
                } else {
                    formSubtaskIds = form.testGroupSubtaskIds.get(i - 1);
                }

                Set<Integer> subtaskIds = formSubtaskIds.stream()
                        .filter(s -> s != null)
                        .collect(Collectors.toSet());

                filledTestData.add(TestGroup.of(i, Lists.transform(testData.get(i).getTestCases(), tc -> TestCase.of(tc.getInput(), tc.getOutput(), subtaskIds))));

                if (!subtaskIds.isEmpty()) {
                    subtasksCount = Math.max(subtasksCount, Collections.max(subtaskIds));
                }
            }
        }

        if (form.subtaskPoints != null) {
            for (int i = 0; i < form.subtaskPoints.size(); i++) {
                if (form.subtaskPoints.get(i) != null) {
                    subtasksCount = Math.max(subtasksCount, i + 1);
                }
            }
        }


        ImmutableList.Builder<Integer> subtaskPoints = ImmutableList.builder();
        for (int i = 0; i < subtasksCount; i++) {
            if (form.subtaskPoints != null && i < form.subtaskPoints.size() && form.subtaskPoints.get(i) != null) {
                subtaskPoints.add(form.subtaskPoints.get(i));
            } else {
                subtaskPoints.add(0);
            }
        }

        return ImmutableList.of(timeLimit, memoryLimit, filledTestData.build(), subtaskPoints.build());
    }

    private List<Integer> getSubtaskIds(TestCase testCase, int subtasksCount) {
        List<Integer> subtaskIds = Lists.newArrayList();

        Set<Integer> configSubtaskIds = testCase.getSubtaskIds();

        for (int i = 0; i < subtasksCount; i++) {
            if (configSubtaskIds.contains(i + 1)) {
                subtaskIds.add(i + 1);
            } else {
                subtaskIds.add(null);
            }
        }
        return subtaskIds;
    }
}
