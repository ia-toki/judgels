package judgels.uriel.api.contest.announcement;

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

@Path("/api/v2/contests/{contestJid}/announcements")
public interface ContestAnnouncementService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestAnnouncementsResponse getAnnouncements(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("page") Optional<Integer> page);

    @POST
    @Path("/")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    ContestAnnouncement createAnnouncement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestAnnouncementData data);

    @PUT
    @Path("/{announcementJid}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    ContestAnnouncement updateAnnouncement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("announcementJid") String announcementJid,
            ContestAnnouncementData data);
}
