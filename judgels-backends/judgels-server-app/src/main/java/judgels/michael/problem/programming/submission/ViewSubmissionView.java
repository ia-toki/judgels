package judgels.michael.problem.programming.submission;

import java.util.Map;
import java.util.Optional;
import judgels.api.profile.Profile;
import judgels.api.submission.programming.Submission;
import judgels.grading.api.GradingResultDetails;
import judgels.grading.api.OutputOnlyOverrides;
import judgels.grading.api.SourceFile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ViewSubmissionView extends TemplateView {
    private final Submission submission;
    private final Optional<GradingResultDetails> details;
    private final Map<String, SourceFile> sourceFiles;
    private final Profile profile;
    private final String gradingLanguageName;

    public ViewSubmissionView(
            HtmlTemplate template,
            Submission submission,
            Optional<GradingResultDetails> details,
            Map<String, SourceFile> sourceFiles,
            Profile profile,
            String gradingLanguageName) {

        super("viewSubmissionView.ftl", template);
        this.submission = submission;
        this.details = details;
        this.sourceFiles = sourceFiles;
        this.profile = profile;
        this.gradingLanguageName = gradingLanguageName;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Optional<GradingResultDetails> getDetails() {
        return details;
    }

    public Map<String, SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getGradingLanguageName() {
        return gradingLanguageName;
    }

    public boolean getIsOutputOnly() {
        return gradingLanguageName.equals(OutputOnlyOverrides.NAME);
    }

    public String getSourceFileContent(SourceFile sourceFile) {
        return new String(sourceFile.getContent());
    }
}
