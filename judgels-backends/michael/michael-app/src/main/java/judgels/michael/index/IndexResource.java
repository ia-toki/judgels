package judgels.michael.index;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.sessions.Flash;
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
import judgels.michael.template.HtmlTemplate;

@Path("/")
public class IndexResource extends BaseResource {
    private static final String POST_LOGIN_URL = "/problems";

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
    public Response logIn(@CookieParam("JOPHIEL_TOKEN") String token, LoginForm form) {
        if (token != null) {
            if (sessionStore.getSessionByToken(token).isPresent()) {
                return redirect(POST_LOGIN_URL);
            }
        }

        HtmlTemplate template = newTemplate();
        template.setTitle("Log in");
        return Response.ok(new LoginView(template, form)).build();
    }

    @POST
    @Path("/login")
    @UnitOfWork
    public Response postLogIn(
            @Context UriInfo uriInfo,
            @io.dropwizard.jersey.sessions.Session Flash<String> flash,
            @BeanParam LoginForm form) {

        Optional<User> maybeUser = userStore.getUserByUsernameAndPassword(form.username, form.password);
        if (!maybeUser.isPresent()) {
            form.globalError = "Username or password incorrect.";
            return logIn(null, form);
        }

        User user = maybeUser.get();
        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            form.globalError = "Username or password incorrect.";
            return logIn(null, form);
        }

        Session session = sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
        return Response
                .seeOther(URI.create(POST_LOGIN_URL))
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
}
