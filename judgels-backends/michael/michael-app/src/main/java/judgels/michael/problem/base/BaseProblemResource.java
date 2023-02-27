package judgels.michael.problem.base;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;

public abstract class BaseProblemResource extends BaseResource {
    protected HtmlTemplate newProblemsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("problems");
        return template;
    }

    protected HtmlTemplate newProblemTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = this.newProblemsTemplate(actor);
        template.setTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.addMainTab("general", "General", "/problems/" + problem.getId());
        template.addMainTab("statements", "Statements", "/problems/" + problem.getType().name().toLowerCase() + "/" + problem.getId() + "/statements");
        return template;
    }

    protected HtmlTemplate newProblemStatementTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("statements");
        return template;
    }
}
