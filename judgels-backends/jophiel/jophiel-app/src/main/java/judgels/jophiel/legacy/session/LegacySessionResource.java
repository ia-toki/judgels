package judgels.jophiel.legacy.session;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.util.concurrent.Uninterruptibles;
import io.dropwizard.hibernate.UnitOfWork;
import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.registration.UserRegistrationEmailStore;
import judgels.service.RandomCodeGenerator;
import judgels.service.api.actor.AuthHeader;

@Path("/api/legacy/session")
public class LegacySessionResource {
    private static final String COOKIE_NAME = "JOPHIEL_USER_JID";

    private final SessionStore sessionStore;
    private final UserStore userStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject
    public LegacySessionResource(
            SessionStore sessionStore,
            UserStore userStore,
            UserRegistrationEmailStore userRegistrationEmailStore) {
        this.sessionStore = sessionStore;
        this.userStore = userStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
    }

    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public LegacySession logIn(Credentials credentials) {
        User user = userStore.findUserByUsernameAndPassword(credentials.getUsernameOrEmail(), credentials.getPassword())
                .orElseGet(() ->
                    userStore.findUserByEmailAndPassword(credentials.getUsernameOrEmail(), credentials.getPassword())
                    .orElseThrow(ForbiddenException::new));

        checkAllowed(userRegistrationEmailStore.isUserActivated(user.getJid()));

        Session session = sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
        String authCode = RandomCodeGenerator.newCode();
        sessionStore.createAuthCode(session.getToken(), authCode);
        return new LegacySession.Builder()
                .authCode(authCode)
                .token(session.getToken())
                .userJid(session.getUserJid())
                .build();
    }

    @POST
    @Path("/propagate-login")
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public LegacySession propagateLogin(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String token = authHeader.getBearerToken();
        String authCode = RandomCodeGenerator.newCode();
        sessionStore.createAuthCode(token, authCode);
        return new LegacySession.Builder()
                .authCode(authCode)
                .token(token)
                .userJid("unused")
                .build();
    }

    @GET
    @Path("/prepare-post-login/{authCode}/{redirectUri}")
    @UnitOfWork(readOnly = true)
    public Response preparePostLogIn(
            @Context UriInfo uriInfo,
            @PathParam("authCode") String authCode,
            @PathParam("redirectUri") String redirectUri) {

        Optional<Session> session = Optional.empty();

        // Hack: wait until the auth code actually got written to db
        for (int i = 0; i < 3; i++) {
            session = sessionStore.findSessionByAuthCode(authCode);
            if (session.isPresent()) {
                break;
            }
            Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
        }
        if (!session.isPresent()) {
            throw new IllegalArgumentException();
        }

        return Response.seeOther(URI.create(redirectUri))
                .cookie(new NewCookie(
                        COOKIE_NAME,
                        session.get().getUserJid(),
                        "/",
                        uriInfo.getBaseUri().getHost(),
                        null,
                        NewCookie.DEFAULT_MAX_AGE,
                        false,
                        true))
                .build();
    }

    @POST
    @Path("/post-login/{authCode}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Session postLogIn(@PathParam("authCode") String authCode) {
        Session session = sessionStore.findSessionByAuthCode(authCode).orElseThrow(IllegalArgumentException::new);
        sessionStore.deleteAuthCode(authCode);
        return session;
    }

    @GET
    @Path("/is-logged-in")
    public Response isLoggedIn(
            @CookieParam(COOKIE_NAME) String userJidFromCookie,
            @QueryParam("userJid") String userJid,
            @QueryParam("callback") String callback) {

        boolean res = userJidFromCookie != null && userJidFromCookie.equals(userJid);
        return Response.ok(callback + "(" + res + ");", "application/javascript").build();
    }

    @GET
    @Path("/post-logout/{redirectUri}")
    @UnitOfWork(readOnly = true)
    public Response postLogout(@Context UriInfo uriInfo, @PathParam("redirectUri") String redirectUri) {
        return Response.seeOther(URI.create(redirectUri))
                .cookie(new NewCookie(
                        COOKIE_NAME,
                        "expired",
                        "/",
                        uriInfo.getBaseUri().getHost(),
                        Cookie.DEFAULT_VERSION,
                        null,
                        NewCookie.DEFAULT_MAX_AGE,
                        new Date(0),
                        false,
                        true))
                .build();
    }
}
