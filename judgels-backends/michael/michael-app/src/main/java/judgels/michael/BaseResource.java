package judgels.michael;

import io.dropwizard.views.View;
import javax.ws.rs.core.Response;
import judgels.michael.actor.Actor;
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
        return template;
    }

    public Response renderView(View view) {
        return Response.ok(view).build();
    }
}
