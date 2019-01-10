package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FilenameUtils;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.FunctionalGradingConfig;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ConfigurableWithAutoPopulation;
import org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox.html.functionalGradingConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.twirl.api.Html;

import java.util.List;

public class FunctionalGradingEngineAdapter extends MultipleSourceFilesBlackBoxGradingEngineAdapter implements ConfigurableWithAutoPopulation {
    @Override
    public Form<?> createFormFromConfig(GradingConfig config) {
        FunctionalGradingConfigForm form = new FunctionalGradingConfigForm();
        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;
        fillMultipleSourceFileBlackBoxGradingConfigFormPartsFromConfig(form, castConfig);

        if (castConfig.getCustomScorer() == null) {
            form.customScorer = "(none)";
        } else {
            form.customScorer = castConfig.getCustomScorer();
        }

        return Form.form(FunctionalGradingConfigForm.class).fill(form);
    }

    @Override
    public Form<?> createEmptyForm() {
        return Form.form(FunctionalGradingConfigForm.class);
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

        return new FunctionalGradingConfig(timeLimit, memoryLimit, testData, sourceFileFieldKeys, customScorer);
    }

    @Override
    public GradingConfig updateConfigWithAutoPopulation(GradingConfig config, List<FileInfo> testDataFiles) {
        ImmutableList.Builder<TestCase> testCases = ImmutableList.builder();
        ImmutableList.Builder<TestCase> sampleTestCases = ImmutableList.builder();

        for (int i = 0; i + 1 < testDataFiles.size(); i++) {
            String in = testDataFiles.get(i).getName();
            String out = testDataFiles.get(i + 1).getName();
            if (isTestCasePair(in, out)) {
                if (in.contains("sample")) {
                    sampleTestCases.add(new TestCase(in, out, ImmutableSet.of(0)));
                } else {
                    testCases.add(new TestCase(in, out, ImmutableSet.of(-1)));
                }
                i++;
            }
        }

        List<TestGroup> testData = ImmutableList.of(new TestGroup(0, sampleTestCases.build()), new TestGroup(-1, testCases.build()));

        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;
        return new FunctionalGradingConfig(castConfig.getTimeLimitInMilliseconds(), castConfig.getMemoryLimitInKilobytes(), testData, castConfig.getSourceFileFieldKeys(), castConfig.getCustomScorer());
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
