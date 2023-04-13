package judgels.michael.resource;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.GitCommit;

public class ListVersionHistoryView extends TemplateView {
    private final List<GitCommit> versions;
    private final Map<String, Profile> profilesMap;
    private final boolean isClean;

    public ListVersionHistoryView(HtmlTemplate template, List<GitCommit> versions, Map<String, Profile> profilesMap, boolean isClean) {
        super("listVersionHistoryView.ftl", template);
        this.versions = versions;
        this.profilesMap = profilesMap;
        this.isClean = isClean;
    }

    public List<GitCommit> getVersions() {
        return versions;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }

    public boolean getIsClean() {
        return isClean;
    }
}
