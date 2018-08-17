package judgels.uriel.api.contest;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests")
public interface ContestService {
    @GET
    @Path("/{contestJid}")
    @Produces(APPLICATION_JSON)
    Contest getContest(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/slug/{contestSlug}")
    @Produces(APPLICATION_JSON)
    Contest getContestBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestSlug") String contestSlug);

    @POST
    @Path("/{contestJid}/virtual")
    void startVirtualContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    Page<Contest> getContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/active")
    @Produces(APPLICATION_JSON)
    List<Contest> getActiveContests(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader);

    @GET
    @Path("/past")
    @Produces(APPLICATION_JSON)
    Page<Contest> getPastContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("page") Optional<Integer> page);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Contest createContest(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ContestData contestData);

    @GET
    @Path("/{contestJid}/description")
    @Produces(APPLICATION_JSON)
    Contest getContestDescription(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);
}
