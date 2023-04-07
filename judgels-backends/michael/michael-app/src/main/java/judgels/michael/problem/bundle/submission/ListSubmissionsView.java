package judgels.michael.problem.bundle.submission;

import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;

public class ListSubmissionsView extends TemplateView {
    private final Page<BundleSubmission> submissions;
    private final Map<String, Profile> profilesMap;
    private final boolean canEdit;

    public ListSubmissionsView(
            HtmlTemplate template,
            Page<BundleSubmission> submissions,
            Map<String, Profile> profilesMap,
            boolean canEdit) {

        super("listSubmissionsView.ftl", template);
        this.submissions = submissions;
        this.profilesMap = profilesMap;
        this.canEdit = canEdit;
    }

    public Page<BundleSubmission> getSubmissions() {
        return submissions;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
