package judgels.michael.login;

import io.dropwizard.views.View;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import judgels.michael.template.HtmlTemplate;

@Path("/login")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {
    @Inject
    public LoginResource() {}

    @GET
    public View logIn() {
        HtmlTemplate template = new HtmlTemplate();
        template.setTitle("Log in");
        return new LoginView(template);
    }
}
