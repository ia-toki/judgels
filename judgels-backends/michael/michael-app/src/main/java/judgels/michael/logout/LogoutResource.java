package judgels.michael.logout;

import io.dropwizard.hibernate.UnitOfWork;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import judgels.jophiel.session.SessionStore;
import judgels.michael.BaseResource;
import judgels.michael.MichaelConfiguration;

@Path("/logout")
public class LogoutResource extends BaseResource {
    private final SessionStore sessionStore;

    @Inject
    public LogoutResource(
            MichaelConfiguration config,
            SessionStore sessionStore) {

        super(config);
        this.sessionStore = sessionStore;
    }

    @GET
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
