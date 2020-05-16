package judgels.jophiel.play;

import com.google.common.util.concurrent.Uninterruptibles;
import io.dropwizard.hibernate.UnitOfWork;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import judgels.jophiel.api.play.PlaySession;
import judgels.jophiel.api.play.PlaySessionService;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionErrors;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.role.UserRoleStore;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserRegistrationEmailStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.service.RandomCodeGenerator;

public class PlaySessionResource implements PlaySessionService {
    private static final String COOKIE_NAME = "JOPHIEL_TOKEN";

    private final SessionStore sessionStore;
    private final UserStore userStore;
    private final UserInfoStore userInfoStore;
    private final UserRoleStore userRoleStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject
    public PlaySessionResource(
            SessionStore sessionStore,
            UserStore userStore,
            UserInfoStore userInfoStore,
            UserRoleStore userRoleStore,
            UserRegistrationEmailStore userRegistrationEmailStore) {
        this.sessionStore = sessionStore;
        this.userStore = userStore;
        this.userInfoStore = userInfoStore;
        this.userRoleStore = userRoleStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
    }

    @UnitOfWork
    public PlaySession logIn(Credentials credentials) {
        User user = userStore.getUserByUsernameAndPassword(credentials.getUsernameOrEmail(), credentials.getPassword())
                    .orElseThrow(ForbiddenException::new);

        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            throw SessionErrors.userNotActivated(user.getEmail());
        }

        UserRole role = userRoleStore.getRole(user.getJid());

        // TODO(fushar): generalize later when there is another Play client
        String playRole = role.getSandalphon().orElse("USER");

        Session session = sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());

        String authCode = RandomCodeGenerator.newCode();
        sessionStore.createAuthCode(session.getToken(), authCode);

        UserInfo info = userInfoStore.getInfo(user.getJid());

        return new PlaySession.Builder()
                .authCode(authCode)
                .token(session.getToken())
                .userJid(session.getUserJid())
                .username(user.getUsername())
                .role(playRole)
                .name(info.getName())
                .build();
    }

    @GET
    @Path("/client-login/{authCode}/{redirectUri}")
    @UnitOfWork
    public Response serviceLogIn(
            @Context UriInfo uriInfo,
            @PathParam("authCode") String authCode,
            @PathParam("redirectUri") String redirectUri) {

        Optional<Session> session = Optional.empty();

        // Hack: wait until the auth code actually got written to db
        for (int i = 0; i < 3; i++) {
            session = sessionStore.getSessionByAuthCode(authCode);
            if (session.isPresent()) {
                break;
            }
            Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
        }
        if (!session.isPresent()) {
            throw new IllegalArgumentException();
        }

        sessionStore.deleteAuthCode(authCode);

        return Response.seeOther(URI.create(redirectUri))
                .cookie(new NewCookie(
                        COOKIE_NAME,
                        session.get().getToken(),
                        "/",
                        uriInfo.getBaseUri().getHost(),
                        null,
                        (int) Duration.ofDays(7).getSeconds(),
                        false,
                        true))
                .build();
    }

    @GET
    @Path("/is-logged-in")
    @UnitOfWork(readOnly = true)
    public Response isLoggedIn(
            @CookieParam(COOKIE_NAME) String token,
            @QueryParam("callback") String callback) {

        boolean res = sessionStore.getSessionByToken(token).isPresent();
        return Response.ok(callback + "(" + res + ");", "application/javascript").build();
    }

    @GET
    @Path("/client-logout/{redirectUri}")
    @UnitOfWork
    public Response serviceLogOut(
            @Context UriInfo uriInfo,
            @CookieParam(COOKIE_NAME) String token,
            @PathParam("redirectUri") String redirectUri) {
        sessionStore.deleteSessionByToken(token);
        return Response.seeOther(URI.create(redirectUri))
                .cookie(new NewCookie(
                        COOKIE_NAME,
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
