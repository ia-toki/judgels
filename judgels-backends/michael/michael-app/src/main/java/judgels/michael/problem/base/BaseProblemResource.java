package judgels.michael.problem.base;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;

public abstract class BaseProblemResource extends BaseResource {
    protected HtmlTemplate newProblemsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("/problems");
        return template;
    }

    protected HtmlTemplate newProblemTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = this.newProblemsTemplate(actor);
        setTitle(template, problem);
        setTabs(template, problem);
        return template;
    }

    private void setTitle(HtmlTemplate template, Problem problem) {
        template.setTitle("#" + problem.getId() + ": " + problem.getSlug());
    }

    private void setTabs(HtmlTemplate template, Problem problem) {
        template.addMainTab("General", "/problems/" + problem.getId());
        template.addMainTab("Statements", "/problems/" + problem.getType().name().toLowerCase() + "/" + problem.getId() + "/statements");
    }
}
