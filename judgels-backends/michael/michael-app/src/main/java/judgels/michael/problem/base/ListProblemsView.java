package judgels.michael.problem.base;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;

public class ListProblemsView extends TemplateView {
    private final Page<Problem> problems;
    private final String filterString;
    private final Map<String, Profile> profilesMap;
    private final List<String> tags;

    public ListProblemsView(HtmlTemplate template, Page<Problem> problems, String filterString, Map<String, Profile> profilesMap, List<String> tags) {
        super("listProblemsView.ftl", template);
        this.problems = problems;
        this.filterString = filterString;
        this.profilesMap = profilesMap;
        this.tags = tags;
    }

    public Page<Problem> getProblems() {
        return problems;
    }

    public String getFilterString() {
        return filterString;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }

    public List<String> getTags() {
        return tags;
    }
}
