package judgels.michael;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.template.HtmlTemplate;

public abstract class BaseResource {
    private final MichaelConfiguration config;

    public BaseResource(MichaelConfiguration config) {
        this.config = config;
    }

    public HtmlTemplate newTemplate() {
        return new HtmlTemplate(config.getName());
    }

    public HtmlTemplate newTemplate(Actor actor) {
        HtmlTemplate template = newTemplate();
        template.setUsername(actor.getUsername());
        template.setAvatarUrl(actor.getAvatarUrl());
        template.addSidebarMenu("Problems", "/problems");
        template.addSidebarMenu("Lessons", "/lessons");
        return template;
    }
}
