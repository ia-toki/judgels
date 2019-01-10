package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.InteractiveWithSubtasksGradingConfig;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ConfigurableWithTokilibFormat;
import org.iatoki.judgels.sandalphon.problem.programming.grading.TokilibFile;
import org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox.html.interactiveWithSubtasksGradingConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.twirl.api.Html;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class InteractiveWithSubtasksGradingEngineAdapter extends SingleSourceFileWithSubtasksBlackBoxGradingEngineAdapter implements ConfigurableWithTokilibFormat {

    @Override
    public Form<?> createFormFromConfig(GradingConfig config) {
        InteractiveWithSubtasksGradingConfigForm form = new InteractiveWithSubtasksGradingConfigForm();
        InteractiveWithSubtasksGradingConfig castConfig = (InteractiveWithSubtasksGradingConfig) config;
        fillSingleSourceFileWithSubtasksBlackBoxGradingConfigFormPartsFromConfig(form, castConfig);

        if (castConfig.getCommunicator() == null) {
            form.communicator = "(none)";
        } else {
            form.communicator = castConfig.getCommunicator();
        }

        return Form.form(InteractiveWithSubtasksGradingConfigForm.class).fill(form);
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

        return new InteractiveWithSubtasksGradingConfig(timeLimit, memoryLimit, testData, subtaskPoints, communicator);
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

        List<TestGroup> testData = Lists.newArrayList();
        for (int i = 0; i <= maxBatchNo; i++) {
            testData.add(new TestGroup(i, Lists.newArrayList()));
        }

        for (TokilibFile file : tokilibFiles) {
            String name = file.filename;
            int batchNo = file.batchNo;
            int tcNo = file.tcNo;

            String filename = name + "_" + batchNo + "_" + tcNo;
            Set<Integer> subtaskIds = Sets.newHashSet();

            if (batchNo == 0) {
                subtaskIds.add(0);
            } else {
                for (int i = batchNo; i <= maxBatchNo; i++) {
                    subtaskIds.add(i);
                }
            }

            TestCase testCase = new TestCase(filename + ".in", null, subtaskIds);

            testData.get(file.batchNo).getTestCases().add(testCase);
        }

        List<Integer> subtaskPoints = Lists.newArrayList();
        for (int i = 1; i <= maxBatchNo; i++) {
            subtaskPoints.add(0);
        }

        InteractiveWithSubtasksGradingConfig castConfig = (InteractiveWithSubtasksGradingConfig) config;

        return new InteractiveWithSubtasksGradingConfig(castConfig.getTimeLimitInMilliseconds(), castConfig.getMemoryLimitInKilobytes(), testData, subtaskPoints, castConfig.getCommunicator());
    }

    @Override
    public Form<?> createEmptyForm() {
        return Form.form(InteractiveWithSubtasksGradingConfigForm.class);
    }

    @Override
    public Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        @SuppressWarnings("unchecked")
        Form<InteractiveWithSubtasksGradingConfigForm> interactiveForm = (Form<InteractiveWithSubtasksGradingConfigForm>) form;

        return interactiveWithSubtasksGradingConfigView.render(interactiveForm, postUpdateGradingConfigCall, testDataFiles, helperFiles);
    }
}
