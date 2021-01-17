package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveWithSubtasksGradingConfig;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ConfigurableWithTokilibFormat;
import org.iatoki.judgels.sandalphon.problem.programming.grading.TokilibFile;
import org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox.html.interactiveWithSubtasksGradingConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.twirl.api.Html;

public final class InteractiveWithSubtasksGradingEngineAdapter extends SingleSourceFileWithSubtasksBlackBoxGradingEngineAdapter implements ConfigurableWithTokilibFormat {

    @Override
    public Form<?> createFormFromConfig(FormFactory formFactory, GradingConfig config) {
        InteractiveWithSubtasksGradingConfigForm form = new InteractiveWithSubtasksGradingConfigForm();
        InteractiveWithSubtasksGradingConfig castConfig = (InteractiveWithSubtasksGradingConfig) config;
        fillSingleSourceFileWithSubtasksBlackBoxGradingConfigFormPartsFromConfig(form, castConfig);

        if (!castConfig.getCommunicator().isPresent()) {
            form.communicator = "(none)";
        } else {
            form.communicator = castConfig.getCommunicator().get();
        }

        return formFactory.form(InteractiveWithSubtasksGradingConfigForm.class).fill(form);
    }

    @Override
    public GradingConfig createConfigFromForm(Form<?> form) {
        @SuppressWarnings("unchecked")
        Form<InteractiveWithSubtasksGradingConfigForm> castForm = (Form<InteractiveWithSubtasksGradingConfigForm>) form;
        InteractiveWithSubtasksGradingConfigForm formData = castForm.get();

        List<Object> parts = createSingleSourceFileWithSubtasksBlackBoxGradingConfigPartsFromForm(formData);

        int timeLimit = (int) parts.get(0);
        int memoryLimit = (int) parts.get(1);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts.get(2);

        @SuppressWarnings("unchecked")
        List<Integer> subtaskPoints = (List<Integer>) parts.get(3);

        String communicator;
        if (formData.communicator.equals("(none)")) {
            communicator = null;
        } else {
            communicator = formData.communicator;
        }

        return new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .subtaskPoints(subtaskPoints)
                .communicator(Optional.ofNullable(communicator))
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
            if (!filenames.contains(filename + ".in")) {
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

            TestCase testCase = TestCase.of(filename + ".in", "", subtaskIds);

            testGroups.get(file.batchNo).add(testCase);
        }

        List<TestGroup> testData = Lists.newArrayList();
        for (int i = 0; i <= maxBatchNo; i++) {
            testData.add(TestGroup.of(i, testGroups.get(i)));
        }

        List<Integer> subtaskPoints = Lists.newArrayList();
        for (int i = 1; i <= maxBatchNo; i++) {
            subtaskPoints.add(0);
        }

        InteractiveWithSubtasksGradingConfig castConfig = (InteractiveWithSubtasksGradingConfig) config;

        return new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(castConfig.getTimeLimit())
                .memoryLimit(castConfig.getMemoryLimit())
                .testData(testData)
                .subtaskPoints(subtaskPoints)
                .communicator(castConfig.getCommunicator())
                .build();
    }

    @Override
    public Form<?> createEmptyForm(FormFactory formFactory) {
        return formFactory.form(InteractiveWithSubtasksGradingConfigForm.class);
    }

    @Override
    public Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        @SuppressWarnings("unchecked")
        Form<InteractiveWithSubtasksGradingConfigForm> interactiveForm = (Form<InteractiveWithSubtasksGradingConfigForm>) form;

        return interactiveWithSubtasksGradingConfigView.render(interactiveForm, postUpdateGradingConfigCall, testDataFiles, helperFiles);
    }
}
