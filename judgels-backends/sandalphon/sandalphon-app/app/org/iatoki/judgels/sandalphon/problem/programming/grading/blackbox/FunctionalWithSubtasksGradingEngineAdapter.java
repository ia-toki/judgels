package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.Subtask;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalWithSubtasksGradingConfig;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ConfigurableWithTokilibFormat;
import org.iatoki.judgels.sandalphon.problem.programming.grading.TokilibFile;
import org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox.html.functionalWithSubtasksGradingConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.twirl.api.Html;

public class FunctionalWithSubtasksGradingEngineAdapter extends MultipleSourceFilesBlackBoxGradingEngineAdapter implements ConfigurableWithTokilibFormat {
    @Override
    public Form<?> createFormFromConfig(FormFactory formFactory, GradingConfig config) {
        FunctionalWithSubtasksGradingConfigForm form = new FunctionalWithSubtasksGradingConfigForm();
        FunctionalWithSubtasksGradingConfig castConfig = (FunctionalWithSubtasksGradingConfig) config;
        fillMultipleSourceFileBlackBoxGradingConfigFormPartsFromConfig(form, castConfig);


        int subtasksCount = Math.max(10, castConfig.getSubtasks().size());

        ImmutableList.Builder<List<Integer>> testGroupSubtaskIds = ImmutableList.builder();

        for (TestGroup testGroup : castConfig.getTestData()) {
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
        for (Subtask subtask : castConfig.getSubtasks()) {
            subtaskPoints.add(subtask.getPoints());
        }
        for (int i = castConfig.getSubtasks().size(); i < subtasksCount; i++) {
            subtaskPoints.add(null);
        }

        form.subtaskPoints = subtaskPoints;

        if (!castConfig.getCustomScorer().isPresent()) {
            form.customScorer = "(none)";
        } else {
            form.customScorer = castConfig.getCustomScorer().get();
        }

        return formFactory.form(FunctionalWithSubtasksGradingConfigForm.class).fill(form);
    }

    @Override
    public Form<?> createEmptyForm(FormFactory formFactory) {
        return formFactory.form(FunctionalWithSubtasksGradingConfigForm.class);
    }

    @Override
    public GradingConfig createConfigFromForm(Form<?> form) {
        @SuppressWarnings("unchecked")
        Form<FunctionalWithSubtasksGradingConfigForm> castForm = (Form<FunctionalWithSubtasksGradingConfigForm>) form;
        FunctionalWithSubtasksGradingConfigForm formData = castForm.get();

        List<Object> parts = createMultipleSourceFileBlackBoxGradingConfigPartsFromForm(formData);

        int timeLimit = (int) parts.get(0);
        int memoryLimit = (int) parts.get(1);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts.get(2);

        @SuppressWarnings("unchecked")
        List<String> sourceFileFieldKeys = (List<String>) parts.get(3);

        int subtasksCount = 0;

        ImmutableList.Builder<TestGroup> filledTestData = ImmutableList.builder();

        for (int i = 0; i < testData.size(); i++) {
            TestGroup testGroup = testData.get(i);

            if (testGroup.getId() == 0) {
                ImmutableList.Builder<TestCase> sampleTestCases = ImmutableList.builder();

                for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                    TestCase sampleTestCase = testGroup.getTestCases().get(j);

                    List<Integer> formSubtaskIds;

                    if (formData.sampleTestCaseSubtaskIds == null || j >= formData.sampleTestCaseSubtaskIds.size()) {
                        formSubtaskIds = ImmutableList.of();
                    } else {
                        formSubtaskIds = formData.sampleTestCaseSubtaskIds.get(j);
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

                if (formData.testGroupSubtaskIds == null || i - 1 >= formData.testGroupSubtaskIds.size()) {
                    formSubtaskIds = ImmutableList.of();
                } else {
                    formSubtaskIds = formData.testGroupSubtaskIds.get(i - 1);
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

        if (formData.subtaskPoints != null) {
            for (int i = 0; i < formData.subtaskPoints.size(); i++) {
                if (formData.subtaskPoints.get(i) != null) {
                    subtasksCount = Math.max(subtasksCount, i + 1);
                }
            }
        }

        ImmutableList.Builder<Integer> subtaskPoints = ImmutableList.builder();
        for (int i = 0; i < subtasksCount; i++) {
            if (formData.subtaskPoints != null && i < formData.subtaskPoints.size() && formData.subtaskPoints.get(i) != null) {
                subtaskPoints.add(formData.subtaskPoints.get(i));
            } else {
                subtaskPoints.add(0);
            }
        }

        String customScorer;
        if (formData.customScorer.equals("(none)")) {
            customScorer = null;
        } else {
            customScorer = formData.customScorer;
        }

        return new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(filledTestData.build())
                .sourceFileFieldKeys(sourceFileFieldKeys)
                .subtaskPoints(subtaskPoints.build())
                .customScorer(Optional.ofNullable(customScorer))
                .build();
    }

    @Override
    public Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        @SuppressWarnings("unchecked")
        Form<FunctionalWithSubtasksGradingConfigForm> castForm = (Form<FunctionalWithSubtasksGradingConfigForm>) form;

        return functionalWithSubtasksGradingConfigView.render(castForm, postUpdateGradingConfigCall, testDataFiles, helperFiles);
    }


    @Override
    public GradingConfig updateConfigWithTokilibFormat(GradingConfig config, List<FileInfo> testDataFiles) {
        Set<String> filenames = Sets.newHashSet(Lists.transform(testDataFiles, f -> f.getName()));
        Set<String> filenamesNoExt = Sets.newHashSet();
        for (String filename : filenames) {
            String[] parts = filename.split("\\.");
            if (parts.length != 2) {
                continue;
            }

            filenamesNoExt.add(parts[0]);
        }

        List<TokilibFile> tokilibFiles = Lists.newArrayList();

        for (String filename : filenamesNoExt) {
            if (!filenames.contains(filename + ".in") || !filenames.contains(filename + ".out")) {
                continue;
            }

            String[] parts = filename.split("_");

            if (parts.length != 3) {
                continue;
            }

            try {
                String name = parts[0];
                int batchNo;
                if (parts[1].equals("sample")) {
                    batchNo = 0;
                } else {
                    batchNo = Integer.parseInt(parts[1]);
                }
                int tcNo = Integer.parseInt(parts[2]);

                tokilibFiles.add(new TokilibFile(name, batchNo, tcNo));
            } catch (NumberFormatException e) {
                // just skip it
            }
        }

        Collections.sort(tokilibFiles);

        int maxBatchNo = 0;
        for (TokilibFile file : tokilibFiles) {
            maxBatchNo = Math.max(maxBatchNo, file.batchNo);
        }

        List<List<TestCase>> testGroups = Lists.newArrayList();
        for (int i = 0; i <= maxBatchNo; i++) {
            testGroups.add(Lists.newArrayList());
        }

        for (TokilibFile file : tokilibFiles) {
            String name = file.filename;
            int batchNo = file.batchNo;
            String batchName = batchNo == 0 ? "sample" : "" + batchNo;
            int tcNo = file.tcNo;

            String filename = name + "_" + batchName + "_" + tcNo;
            Set<Integer> subtaskIds = Sets.newHashSet();

            if (batchNo == 0) {
                subtaskIds.add(0);
            } else {
                for (int i = batchNo; i <= maxBatchNo; i++) {
                    subtaskIds.add(i);
                }
            }

            TestCase testCase = TestCase.of(filename + ".in", filename + ".out", subtaskIds);

            testGroups.get(file.batchNo).add(testCase);
        }

        List<TestGroup> testData = Lists.newArrayList();
        for (int i = 0; i <= maxBatchNo; i++) {
            testData.add(TestGroup.of(i, testGroups.get(i)));
        }

        FunctionalWithSubtasksGradingConfig castConfig = (FunctionalWithSubtasksGradingConfig) config;
        List<String> sourceFileFieldKeys = castConfig.getSourceFileFieldKeys();
        List<Subtask> subtasks = castConfig.getSubtasks();
        List<Integer> subtaskPoints = Lists.newArrayList();
        for (int i = 0; i < maxBatchNo; i++) {
            if (i < subtasks.size()) {
                subtaskPoints.add(subtasks.get(i).getPoints());
            } else {
                subtaskPoints.add(0);
            }
        }

        return new FunctionalWithSubtasksGradingConfig.Builder()
                .from(castConfig)
                .testData(testData)
                .sourceFileFieldKeys(sourceFileFieldKeys)
                .subtaskPoints(subtaskPoints)
                .build();
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
