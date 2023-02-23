package judgels.michael.index;

import io.dropwizard.hibernate.UnitOfWork;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserRegistrationEmailStore;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;

@Path("/")
public class IndexResource extends BaseResource {
    private static final URI POST_LOGIN_URI = URI.create("/problems");

    @Inject protected SessionStore sessionStore;
    @Inject protected UserStore userStore;
    @Inject protected UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject public IndexResource() {}

    @GET
    public Response index() {
        return Response.seeOther(URI.create("/login")).build();
    }

    @GET
    @Path("/login")
    @UnitOfWork(readOnly = true)
    public Response logIn(@CookieParam("JOPHIEL_TOKEN") String token) {
        if (token != null) {
            if (sessionStore.getSessionByToken(token).isPresent()) {
                return Response.seeOther(POST_LOGIN_URI).build();
            }
        }

        return renderLogIn(new LoginForm());
    }

    @POST
    @Path("/login")
    @UnitOfWork
    public Response postLogIn(@Context UriInfo uriInfo, @BeanParam LoginForm form) {
        Optional<User> maybeUser = userStore.getUserByUsernameAndPassword(form.username, form.password);
        if (!maybeUser.isPresent()) {
            return renderLogIn(form.withGlobalError("Username or password incorrect."));
        }

        User user = maybeUser.get();
        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            return renderLogIn(form.withGlobalError("Username or password incorrect."));
        }

        Session session = sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
        return Response
                .seeOther(POST_LOGIN_URI)
                .cookie(new NewCookie(
                        "JOPHIEL_TOKEN",
                        session.getToken(),
                        "/",
                        uriInfo.getBaseUri().getHost(),
                        null,
                        (int) Duration.ofDays(7).getSeconds(),
                        false,
                        true))
                .build();
    }

    @GET
    @Path("/logout")
    @UnitOfWork(readOnly = true)
    public Response logOut(@CookieParam("JOPHIEL_TOKEN") String token, @Context UriInfo uriInfo) {
        sessionStore.deleteSessionByToken(token);
        return Response.seeOther(URI.create("/login"))
                .cookie(new NewCookie(
                        "JOPHIEL_TOKEN",
                        "expired",
                        "/",
                        uriInfo.getBaseUri().getHost(),
                        Cookie.DEFAULT_VERSION,
                        null,
                        (int) Duration.ofDays(7).getSeconds(),
                        new Date(0),
                        false,
                        true))
                .build();
    }

    private Response renderLogIn(HtmlForm form) {
        HtmlTemplate template = newTemplate();
        template.setTitle("Log in");
        return Response.ok(new LoginView(template, (LoginForm) form)).build();
    }
}
