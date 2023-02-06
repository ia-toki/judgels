package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.functional.FunctionalGradingConfig;
import org.apache.commons.io.FilenameUtils;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ConfigurableWithAutoPopulation;
import org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox.html.functionalGradingConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.twirl.api.Html;

public class FunctionalGradingEngineAdapter extends MultipleSourceFilesBlackBoxGradingEngineAdapter implements ConfigurableWithAutoPopulation {
    @Override
    public Form<?> createFormFromConfig(FormFactory formFactory, GradingConfig config) {
        FunctionalGradingConfigForm form = new FunctionalGradingConfigForm();
        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;
        fillMultipleSourceFileBlackBoxGradingConfigFormPartsFromConfig(form, castConfig);

        if (!castConfig.getCustomScorer().isPresent()) {
            form.customScorer = "(none)";
        } else {
            form.customScorer = castConfig.getCustomScorer().get();
        }

        return formFactory.form(FunctionalGradingConfigForm.class).fill(form);
    }

    @Override
    public Form<?> createEmptyForm(FormFactory formFactory) {
        return formFactory.form(FunctionalGradingConfigForm.class);
    }

    @Override
    public GradingConfig createConfigFromForm(Form<?> form) {
        @SuppressWarnings("unchecked")
        Form<FunctionalGradingConfigForm> castForm = (Form<FunctionalGradingConfigForm>) form;
        FunctionalGradingConfigForm formData = castForm.get();

        List<Object> parts = createMultipleSourceFileBlackBoxGradingConfigPartsFromForm(formData);

        int timeLimit = (int) parts.get(0);
        int memoryLimit = (int) parts.get(1);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts.get(2);

        @SuppressWarnings("unchecked")
        List<String> sourceFileFieldKeys = (List<String>) parts.get(3);

        String customScorer;
        if (formData.customScorer.equals("(none)")) {
            customScorer = null;
        } else {
            customScorer = formData.customScorer;
        }

        return new FunctionalGradingConfig.Builder()
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testData(testData)
                .sourceFileFieldKeys(sourceFileFieldKeys)
                .customScorer(Optional.ofNullable(customScorer))
                .build();
    }

    @Override
    public GradingConfig updateConfigWithAutoPopulation(GradingConfig config, List<FileInfo> testDataFiles) {
        ImmutableList.Builder<TestCase> testCases = ImmutableList.builder();
        ImmutableList.Builder<TestCase> sampleTestCases = ImmutableList.builder();

        int i;
        for (i = 0; i + 1 < testDataFiles.size(); i++) {
            String in = testDataFiles.get(i).getName();
            String out = testDataFiles.get(i + 1).getName();
            if (isTestCasePair(in, out)) {
                if (in.contains("sample")) {
                    sampleTestCases.add(TestCase.of(in, out, ImmutableSet.of(0)));
                } else {
                    testCases.add(TestCase.of(in, out, ImmutableSet.of(-1)));
                }
                i++;
            }
        }

        List<TestGroup> testData = ImmutableList.of(TestGroup.of(0, sampleTestCases.build()), TestGroup.of(-1, testCases.build()));

        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;

        return new FunctionalGradingConfig.Builder()
                .from(castConfig)
                .testData(testData)
                .build();
    }

    @Override
    public Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        @SuppressWarnings("unchecked")
        Form<FunctionalGradingConfigForm> castForm = (Form<FunctionalGradingConfigForm>) form;

        return functionalGradingConfigView.render(castForm, postUpdateGradingConfigCall, testDataFiles, helperFiles);
    }

    private boolean isTestCasePair(String in, String out) {
        String inBaseName = FilenameUtils.getBaseName(in);
        String inExtension = FilenameUtils.getExtension(in);

        String outBaseName = FilenameUtils.getBaseName(out);
        String outExtension = FilenameUtils.getExtension(out);

        return inBaseName.equals(outBaseName) && inExtension.equals("in") && outExtension.equals("out");
    }
}
