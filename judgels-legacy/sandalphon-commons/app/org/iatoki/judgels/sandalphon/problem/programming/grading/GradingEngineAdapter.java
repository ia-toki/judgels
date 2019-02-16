package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.sandalphon.api.problem.ProblemStatement;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.SubmissionSource;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import play.api.mvc.Call;
import play.data.Form;
import play.twirl.api.Html;

import java.util.List;
import java.util.Set;

public interface GradingEngineAdapter {

    Set<String> getSupportedGradingEngineNames();

    Form<?> createFormFromConfig(GradingConfig config);

    Form<?> createEmptyForm();

    GradingConfig createConfigFromForm(Form<?> form);

    Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles);

    Html renderViewStatement(String postSubmitUri, ProblemStatement statement, GradingConfig config, String engine, Set<String> allowedGradingLanguage, String reasonNotAllowedToSubmit);

    Html renderViewSubmission(ProgrammingSubmission submission, SubmissionSource submissionSource, String authorName, String problemAlias, String problemName, String gradingLanguage, String contestName);
}
