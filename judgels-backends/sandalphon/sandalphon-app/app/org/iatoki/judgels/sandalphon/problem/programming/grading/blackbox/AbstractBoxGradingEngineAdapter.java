package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.submission.programming.Submission;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapter;
import org.iatoki.judgels.sandalphon.problem.programming.statement.blackbox.html.blackBoxViewStatementView;
import org.iatoki.judgels.sandalphon.problem.programming.submission.blackbox.html.blackBoxViewSubmissionView;
import play.twirl.api.Html;

public abstract class AbstractBoxGradingEngineAdapter implements GradingEngineAdapter {
    @Override
    public Set<String> getSupportedGradingEngineNames() {
        String name = getClass().getSimpleName();
        return ImmutableSet.of(name.substring(0, name.length() - "GradingEngineAdapter".length()));
    }

    @Override
    public Html renderViewStatement(String postSubmitUri, ProblemStatement statement, GradingConfig config, String engine, Set<String> allowedGradingLanguageNames, String reasonNotAllowedToSubmit) {
        return blackBoxViewStatementView.render(postSubmitUri, statement, config, engine, allowedGradingLanguageNames, reasonNotAllowedToSubmit);
    }

    @Override
    public Html renderViewSubmission(Submission submission, SubmissionSource submissionSource, Profile profile, String problemAlias, String problemName, String gradingLanguageName, String contestName) {
        GradingResultDetails details = null;
        if (submission.getLatestGrading().isPresent() && submission.getLatestGrading().get().getDetails().isPresent()) {
            details = submission.getLatestGrading().get().getDetails().get();
        }
        return blackBoxViewSubmissionView.render(submission, details, submissionSource.getSubmissionFiles(), profile, problemAlias, problemName, gradingLanguageName, contestName);
    }

    protected final void fillAbstractBlackBoxGradingFormPartsFromConfig(AbstractBlackBoxGradingConfigForm form, GradingConfig config) {
        form.timeLimit = config.getTimeLimit();
        form.memoryLimit = config.getMemoryLimit();

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
            sampleTestCases.add(TestCase.of(form.sampleTestCaseInputs.get(i), form.sampleTestCaseOutputs.get(i), Sets.newHashSet() /* placeholder */));
        }

        ImmutableList.Builder<TestGroup> testData = ImmutableList.builder();
        testData.add(TestGroup.of(0, sampleTestCases.build()));

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
                testCases.add(TestCase.of(form.testCaseInputs.get(i).get(j), form.testCaseOutputs.get(i).get(j), ImmutableSet.of() /* placeholder */));
            }


            testData.add(TestGroup.of(-1 /* placeholder */, testCases.build()));
        }

        return ImmutableList.of(timeLimit, memoryLimit, testData.build());
    }
}
