package judgels.uriel.api.contest;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.dump.ContestsDump;
import judgels.uriel.api.contest.dump.ExportContestsDumpData;
import judgels.uriel.api.contest.dump.ImportContestsDumpResponse;

@Path("/api/v2/contests")
public interface ContestService {
    @GET
    @Path("/{contestJid}")
    @Produces(APPLICATION_JSON)
    Contest getContest(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);

    @POST
    @Path("/{contestJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Contest updateContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestUpdateData data);

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

    @PUT
    @Path("/{contestJid}/virtual/reset")
    void resetVirtualContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestsResponse getContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/active")
    @Produces(APPLICATION_JSON)
    ActiveContestsResponse getActiveContests(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Contest createContest(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ContestCreateData data);

    @GET
    @Path("/{contestJid}/description")
    @Produces(APPLICATION_JSON)
    ContestDescription getContestDescription(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);

    @POST
    @Path("/{contestJid}/description")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ContestDescription updateContestDescription(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestDescription description);

    @POST
    @Path("/export")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ContestsDump exportDump(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ExportContestsDumpData data);

    @POST
    @Path("/import")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ImportContestsDumpResponse importDump(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ContestsDump contestsDump);
}
