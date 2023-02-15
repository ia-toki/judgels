package judgels.michael;

import io.dropwizard.views.View;
import javax.ws.rs.core.Response;
import judgels.michael.template.HtmlTemplate;

public abstract class BaseResource {
    private final MichaelConfiguration config;

    public BaseResource(MichaelConfiguration config) {
        this.config = config;
    }

    public HtmlTemplate newTemplate() {
        return new HtmlTemplate(config.getName());
    }

    public Response renderView(View view) {
        return Response.ok(view).build();
    }
}
