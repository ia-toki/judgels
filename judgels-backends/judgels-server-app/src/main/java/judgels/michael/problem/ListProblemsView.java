package judgels.michael.problem;

import java.util.Map;
import java.util.Set;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;

public class ListProblemsView extends TemplateView {
    private final Page<Problem> problems;
    private final String termFilter;
    private final Set<String> tagsFilter;
    private final Map<String, Profile> profilesMap;

    public ListProblemsView(HtmlTemplate template, Page<Problem> problems, String termFilter, Set<String> tagsFilter, Map<String, Profile> profilesMap) {
        super("listProblemsView.ftl", template);
        this.problems = problems;
        this.termFilter = termFilter;
        this.tagsFilter = tagsFilter;
        this.profilesMap = profilesMap;
    }

    public Page<Problem> getProblems() {
        return problems;
    }

    public String getTermFilter() {
        return termFilter;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }

    public Set<String> getTagsFilter() {
        return tagsFilter;
    }
}
