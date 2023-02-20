package judgels.michael.problem.base;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.MichaelConfiguration;
import judgels.michael.template.HtmlTemplate;

public abstract class BaseProblemResource extends BaseResource {
    public BaseProblemResource(MichaelConfiguration config) {
        super(config);
    }

    @Override
    public HtmlTemplate newTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("/problems");
        return template;
    }
}
