package judgels.uriel.api.contest.announcement;

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
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/{contestJid}/announcements")
public interface ContestAnnouncementService {
    @GET
    @Path("/published")
    @Produces(APPLICATION_JSON)
    List<ContestAnnouncement> getPublishedAnnouncements(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);

    @POST
    @Path("/")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    ContestAnnouncement createAnnouncement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestAnnouncementData announcementData);

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    ContestAnnouncementConfig getAnnouncementConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);
}
