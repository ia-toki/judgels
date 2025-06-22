package judgels.michael.index;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import judgels.contrib.jophiel.auth.google.GoogleAuth;
import judgels.contrib.jophiel.user.registration.UserRegistrationEmailStore;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;

@Path("/")
public class IndexResource extends BaseResource {
    private static final String POST_LOGIN_URL = "/problems";

    @Inject protected Optional<GoogleAuth> googleAuth;
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
    public Response login(@CookieParam("JUDGELS_TOKEN") String token) {
        if (token != null) {
            if (sessionStore.getSessionByToken(token).isPresent()) {
                return redirect(POST_LOGIN_URL);
            }
        }
        return ok(renderLogin(new LoginForm()));
    }

    private View renderLogin(LoginForm form) {
        HtmlTemplate template = newTemplate();
        template.setContentLayoutClassName("login-layout");
        template.setTitle("Log in");
        return new LoginView(template, form, googleAuth.isPresent());
    }

    @POST
    @Path("/login")
    @UnitOfWork
    public Response logIn(@Context UriInfo uriInfo, @BeanParam LoginForm form) {
        Optional<User> maybeUser = userStore.getUserByUsernameAndPassword(form.username, form.password);
        if (!maybeUser.isPresent()) {
            form.globalError = "Username or password incorrect.";
            return ok(renderLogin(form));
        }

        User user = maybeUser.get();
        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            form.globalError = "Username or password incorrect.";
            return ok(renderLogin(form));
        }

        Session session = sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
        return Response
                .seeOther(URI.create(POST_LOGIN_URL))
                .cookie(new NewCookie(
                        "JUDGELS_TOKEN",
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
    public Response logOut(@CookieParam("JUDGELS_TOKEN") String token, @Context UriInfo uriInfo) {
        sessionStore.deleteSessionByToken(token);
        return Response.seeOther(URI.create("/login"))
                .cookie(new NewCookie(
                        "JUDGELS_TOKEN",
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

    @POST
    @Path("/switchLanguage")
    public Response switchLanguage(@Context HttpServletRequest req, @FormParam("language") String language) {
        setCurrentStatementLanguage(req, language);
        String referer = Optional.ofNullable(req.getHeader("Referer")).orElse("");
        return redirect(referer);
    }
}
