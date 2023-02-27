package judgels.michael;

import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.template.HtmlTemplate;

public abstract class BaseResource {
    @Inject protected MichaelConfiguration config;

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
