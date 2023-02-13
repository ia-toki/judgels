package judgels.michael.login;

import io.dropwizard.views.View;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import judgels.michael.BaseResource;
import judgels.michael.MichaelConfiguration;
import judgels.michael.template.HtmlTemplate;

@Path("/login")
@Produces(MediaType.TEXT_HTML)
public class LoginResource extends BaseResource {
    @Inject
    public LoginResource(MichaelConfiguration config) {
        super(config);
    }

    @GET
    public View logIn() {
        HtmlTemplate template = newTemplate();
        template.setTitle("Log in");
        return new LoginView(template);
    }

    @POST
    public String postLogIn(@FormParam("username") String username) {
        return username;
    }
}
