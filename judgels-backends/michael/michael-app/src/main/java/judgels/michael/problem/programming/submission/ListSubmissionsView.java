package judgels.michael.problem.programming.submission;

import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;

public class ListSubmissionsView extends TemplateView {
    private final Page<Submission> submissions;
    private final Map<String, Profile> profilesMap;
    private final Map<String, String> languagesMap;

    public ListSubmissionsView(
            HtmlTemplate template,
            Page<Submission> submissions,
            Map<String, Profile> profilesMap,
            Map<String, String> languagesMap) {

        super("listSubmissionsView.ftl", template);
        this.submissions = submissions;
        this.profilesMap = profilesMap;
        this.languagesMap = languagesMap;
    }

    public Page<Submission> getSubmissions() {
        return submissions;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }

    public Map<String, String> getLanguagesMap() {
        return languagesMap;
    }
}
