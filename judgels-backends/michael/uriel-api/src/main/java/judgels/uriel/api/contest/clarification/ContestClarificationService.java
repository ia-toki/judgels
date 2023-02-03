package judgels.uriel.api.contest.clarification;

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

@Path("/api/v2/contests/{contestJid}/clarifications")
public interface ContestClarificationService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ContestClarification createClarification(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestClarificationData data);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestClarificationsResponse getClarifications(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("status") Optional<String> status,
            @QueryParam("language") Optional<String> language,
            @QueryParam("page") Optional<Integer> page);

    @PUT
    @Path("/{clarificationJid}/answer")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ContestClarification answerClarification(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("clarificationJid") String clarificationJid,
            ContestClarificationAnswerData data);
}
