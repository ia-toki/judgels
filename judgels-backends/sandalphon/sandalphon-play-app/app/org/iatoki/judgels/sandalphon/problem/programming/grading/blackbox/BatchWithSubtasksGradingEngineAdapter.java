package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.Subtask;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.batch.BatchWithSubtasksGradingConfig;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ConfigurableWithTokilibFormat;
import org.iatoki.judgels.sandalphon.problem.programming.grading.TokilibFile;
import org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox.html.batchWithSubtasksGradingConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.twirl.api.Html;

public final class BatchWithSubtasksGradingEngineAdapter extends SingleSourceFileWithSubtasksBlackBoxGradingEngineAdapter implements ConfigurableWithTokilibFormat {

    @Override
    public Form<?> createFormFromConfig(FormFactory formFactory, GradingConfig config) {
        BatchWithSubtasksGradingConfigForm form = new BatchWithSubtasksGradingConfigForm();
        BatchWithSubtasksGradingConfig castConfig = (BatchWithSubtasksGradingConfig) config;
        fillSingleSourceFileWithSubtasksBlackBoxGradingConfigFormPartsFromConfig(form, castConfig);

        if (!castConfig.getCustomScorer().isPresent()) {
            form.customScorer = "(none)";
        } else {
            form.customScorer = castConfig.getCustomScorer().get();
        }

        return formFactory.form(BatchWithSubtasksGradingConfigForm.class).fill(form);
    }

    @Override
    public GradingConfig createConfigFromForm(Form<?> form) {
        @SuppressWarnings("unchecked")
        Form<BatchWithSubtasksGradingConfigForm> castForm = (Form<BatchWithSubtasksGradingConfigForm>) form;
        BatchWithSubtasksGradingConfigForm formData = castForm.get();

        List<Object> parts = createSingleSourceFileWithSubtasksBlackBoxGradingConfigPartsFromForm(formData);

        int timeLimit = (int) parts.get(0);
        int memoryLimit = (int) parts.get(1);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts.get(2);

        @SuppressWarnings("unchecked")
        List<Integer> subtaskPoints = (List<Integer>) parts.get(3);

        String customScorer;
        if (formData.customScorer.equals("(none)")) {
            customScorer = null;
        } else {
            customScorer = formData.customScorer;
        }

        return new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .subtaskPoints(subtaskPoints)
                .customScorer(Optional.ofNullable(customScorer))
                .build();
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

        BatchWithSubtasksGradingConfig castConfig = (BatchWithSubtasksGradingConfig) config;
        List<Subtask> subtasks = castConfig.getSubtasks();
        List<Integer> subtaskPoints = Lists.newArrayList();
        for (int i = 0; i < maxBatchNo; i++) {
            if (i < subtasks.size()) {
                subtaskPoints.add(subtasks.get(i).getPoints());
            } else {
                subtaskPoints.add(0);
            }
        }

        return new BatchWithSubtasksGradingConfig.Builder()
                .from(castConfig)
                .testData(testData)
                .subtaskPoints(subtaskPoints)
                .build();
    }

    @Override
    public Form<?> createEmptyForm(FormFactory formFactory) {
        return formFactory.form(BatchWithSubtasksGradingConfigForm.class);
    }

    @Override
    public Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        @SuppressWarnings("unchecked")
        Form<BatchWithSubtasksGradingConfigForm> castForm = (Form<BatchWithSubtasksGradingConfigForm>) form;

        return batchWithSubtasksGradingConfigView.render(castForm, postUpdateGradingConfigCall, testDataFiles, helperFiles);
    }
}
