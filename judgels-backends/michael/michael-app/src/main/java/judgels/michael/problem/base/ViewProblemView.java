package judgels.michael.problem.base;

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

    public ViewProblemView(
            HtmlTemplate template,
            Problem problem,
            Profile profile,
            String writerUsernames,
            String developerUsernames,
            String testerUsernames,
            String editorialistUsernames) {

        super("viewProblemView.ftl", template);
        this.problem = problem;
        this.profile = profile;
        this.writerUsernames = writerUsernames;
        this.developerUsernames = developerUsernames;
        this.testerUsernames = testerUsernames;
        this.editorialistUsernames = editorialistUsernames;
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
}
