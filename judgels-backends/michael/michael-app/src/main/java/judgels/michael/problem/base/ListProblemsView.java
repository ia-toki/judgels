package judgels.michael.problem.base;

import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;

public class ListProblemsView extends TemplateView {
    private final Page<Problem> problems;
    private final Map<String, Profile> profilesMap;

    public ListProblemsView(HtmlTemplate template, Page<Problem> problems, Map<String, Profile> profilesMap) {
        super("listProblemsView.ftl", template);
        this.problems = problems;
        this.profilesMap = profilesMap;
    }

    public Page<Problem> getProblems() {
        return problems;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }
}
