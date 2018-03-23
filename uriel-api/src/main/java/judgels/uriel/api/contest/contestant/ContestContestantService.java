package judgels.uriel.api.contest.contestant;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.ContestContestant;

@Path("/api/v2/contests")
public interface ContestContestantService {
    @GET
    @Path("/{contestJid}/contestants")
    @Produces(APPLICATION_JSON)
    Page<ContestContestant> getContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    @POST
    @Path("/{contestJid}/contestants")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    List<String> addContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            List<String> contestantJids);
}
