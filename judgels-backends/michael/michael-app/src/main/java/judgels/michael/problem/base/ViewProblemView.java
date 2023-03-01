package judgels.michael.problem.base;

import java.util.List;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.Problem;

public class ViewProblemView extends TemplateView {
    private final Problem problem;
    private final Profile profile;
    private final String writerUsernames;
    private final String developerUsernames;
    private final String testerUsernames;
    private final String editorialistUsernames;
    private final List<String> tags;

    public ViewProblemView(
            HtmlTemplate template,
            Problem problem,
            Profile profile,
            String writerUsernames,
            String developerUsernames,
            String testerUsernames,
            String editorialistUsernames,
            List<String> tags) {

        super("viewProblemView.ftl", template);
        this.problem = problem;
        this.profile = profile;
        this.writerUsernames = writerUsernames;
        this.developerUsernames = developerUsernames;
        this.testerUsernames = testerUsernames;
        this.editorialistUsernames = editorialistUsernames;
        this.tags = tags;
    }

    public Problem getProblem() {
        return problem;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getWriterUsernames() {
        return writerUsernames;
    }

    public String getDeveloperUsernames() {
        return developerUsernames;
    }

    public String getTesterUsernames() {
        return testerUsernames;
    }

    public String getEditorialistUsernames() {
        return editorialistUsernames;
    }

    public List<String> getTags() {
        return tags;
    }
}
