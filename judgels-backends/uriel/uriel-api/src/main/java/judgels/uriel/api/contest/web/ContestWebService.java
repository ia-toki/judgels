package judgels.uriel.api.contest.web;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contest-web")
public interface ContestWebService {
    @GET
    @Path("/slug/{contestSlug}/with-config")
    @Produces(APPLICATION_JSON)
    ContestWithWebConfig getContestBySlugWithWebConfig(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestSlug") String contestSlug);

    @GET
    @Path("/{contestJid}/with-config")
    @Produces(APPLICATION_JSON)
    ContestWithWebConfig getContestByJidWithWebConfig(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/{contestJid}/config")
    @Produces(APPLICATION_JSON)
    ContestWebConfig getWebConfig(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);
}
