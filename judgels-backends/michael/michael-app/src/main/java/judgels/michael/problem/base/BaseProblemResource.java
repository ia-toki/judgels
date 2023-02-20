package judgels.michael.problem.base;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;

public abstract class BaseProblemResource extends BaseResource {
    @Override
    public HtmlTemplate newTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("/problems");
        return template;
    }
}
