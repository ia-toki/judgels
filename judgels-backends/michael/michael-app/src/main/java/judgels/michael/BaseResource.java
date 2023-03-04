package judgels.michael;

import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.role.RoleChecker;

public abstract class BaseResource {
    @Inject protected MichaelConfiguration config;
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;

    protected HtmlTemplate newTemplate() {
        return new HtmlTemplate(config.getName());
    }

    protected HtmlTemplate newTemplate(Actor actor) {
        HtmlTemplate template = newTemplate();
        template.setUsername(actor.getUsername());
        template.setAvatarUrl(actor.getAvatarUrl());
        template.addSidebarMenu("problems", "Problems", "/problems");
        template.addSidebarMenu("lessons", "Lessons", "/lessons");
        return template;
    }
}
