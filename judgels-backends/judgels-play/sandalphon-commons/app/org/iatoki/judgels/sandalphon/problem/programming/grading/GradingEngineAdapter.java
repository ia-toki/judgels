package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.submission.programming.Submission;
import org.iatoki.judgels.FileInfo;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.twirl.api.Html;

import java.util.List;
import java.util.Set;

public interface GradingEngineAdapter {

    Set<String> getSupportedGradingEngineNames();

    Form<?> createFormFromConfig(FormFactory formFactory, GradingConfig config);

    Form<?> createEmptyForm(FormFactory formFactory);

    GradingConfig createConfigFromForm(Form<?> form);

    Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles);

    Html renderViewStatement(String postSubmitUri, ProblemStatement statement, GradingConfig config, String engine, Set<String> allowedGradingLanguage, String reasonNotAllowedToSubmit);

    Html renderViewSubmission(Submission submission, SubmissionSource submissionSource, String authorName, String problemAlias, String problemName, String gradingLanguage, String contestName);
}
