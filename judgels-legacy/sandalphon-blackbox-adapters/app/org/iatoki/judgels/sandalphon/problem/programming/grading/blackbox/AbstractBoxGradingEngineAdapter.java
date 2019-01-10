package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.SubmissionSource;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.AbstractBlackBoxGradingConfig;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatement;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapter;
import org.iatoki.judgels.sandalphon.problem.programming.statement.blackbox.html.blackBoxViewStatementView;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.blackbox.html.blackBoxViewSubmissionView;
import play.twirl.api.Html;

import java.util.List;
import java.util.Set;

public abstract class AbstractBoxGradingEngineAdapter implements GradingEngineAdapter {

    @Override
    public Set<String> getSupportedGradingEngineNames() {
        String name = getClass().getSimpleName();
        return ImmutableSet.of(name.substring(0, name.length() - "GradingEngineAdapter".length()));
    }

    @Override
    public Html renderViewStatement(String postSubmitUri, ProblemStatement statement, GradingConfig config, String engine, Set<String> allowedGradingLanguageNames, String reasonNotAllowedToSubmit) {
        BlackBoxGradingConfig blackBoxConfig = (BlackBoxGradingConfig) config;
        return blackBoxViewStatementView.render(postSubmitUri, statement, blackBoxConfig, engine, allowedGradingLanguageNames, reasonNotAllowedToSubmit);
    }

    @Override
    public Html renderViewSubmission(ProgrammingSubmission submission, SubmissionSource submissionSource, String authorName, String problemAlias, String problemName, String gradingLanguageName, String contestName) {
        String errorMessage;
        BlackBoxGradingResultDetails details;
        if (submission.getLatestVerdict().getCode().equals("!!!")) {
            errorMessage = submission.getLatestDetails();
            details = null;
        } else {
            errorMessage = null;
            details = new Gson().fromJson(submission.getLatestDetails(), BlackBoxGradingResultDetails.class);
        }

        return blackBoxViewSubmissionView.render(submission, errorMessage, details, submissionSource.getSubmissionFiles(), authorName, problemAlias, problemName, gradingLanguageName, contestName);
    }

    protected final void fillAbstractBlackBoxGradingFormPartsFromConfig(AbstractBlackBoxGradingConfigForm form, AbstractBlackBoxGradingConfig config) {
        form.timeLimit = config.getTimeLimitInMilliseconds();
        form.memoryLimit = config.getMemoryLimitInKilobytes();

        ImmutableList.Builder<List<String>> testCasesInputs = ImmutableList.builder();
        ImmutableList.Builder<List<String>> testCaseOutputs = ImmutableList.builder();

        for (TestGroup testGroup : config.getTestData()) {
            if (testGroup.getId() == 0) {
                form.sampleTestCaseInputs = Lists.transform(testGroup.getTestCases(), tc -> tc.getInput());
                form.sampleTestCaseOutputs = Lists.transform(testGroup.getTestCases(), tc -> tc.getOutput());
            } else {
                testCasesInputs.add(Lists.transform(testGroup.getTestCases(), tc -> tc.getInput()));
                testCaseOutputs.add(Lists.transform(testGroup.getTestCases(), tc -> tc.getOutput()));
            }
        }

        form.testCaseInputs = testCasesInputs.build();
        form.testCaseOutputs = testCaseOutputs.build();
    }

    protected final List<Object> createAbstractBlackBoxGradingConfigPartsFromForm(AbstractBlackBoxGradingConfigForm form) {
        int timeLimit = form.timeLimit;
        int memoryLimit = form.memoryLimit;

        ImmutableList.Builder<TestCase> sampleTestCases = ImmutableList.builder();

        int sampleTestCasesCount = 0;
        if (form.sampleTestCaseInputs != null) {
            sampleTestCasesCount = form.sampleTestCaseInputs.size();
        }

        for (int i = 0; i < sampleTestCasesCount; i++) {
            sampleTestCases.add(new TestCase(form.sampleTestCaseInputs.get(i), form.sampleTestCaseOutputs.get(i), null /* placeholder */));
        }

        ImmutableList.Builder<TestGroup> testData = ImmutableList.builder();
        testData.add(new TestGroup(0, sampleTestCases.build()));

        int testDataGroupsCount = 0;
        if (form.testCaseInputs != null) {
            testDataGroupsCount = form.testCaseInputs.size();
        }

        for (int i = 0; i < testDataGroupsCount; i++) {
            ImmutableList.Builder<TestCase> testCases = ImmutableList.builder();

            int testCasesCount = 0;
            if (form.testCaseInputs.get(i) != null) {
                testCasesCount = form.testCaseInputs.get(i).size();
            }

            for (int j = 0; j < testCasesCount; j++) {
                testCases.add(new TestCase(form.testCaseInputs.get(i).get(j), form.testCaseOutputs.get(i).get(j), null /* placeholder */));
            }


            testData.add(new TestGroup(-1 /* placeholder */, testCases.build()));
        }

        return ImmutableList.of(timeLimit, memoryLimit, testData.build());
    }
}
