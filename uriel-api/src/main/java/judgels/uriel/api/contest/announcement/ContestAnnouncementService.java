package judgels.uriel.api.contest.announcement;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/{contestJid}/announcements")
public interface ContestAnnouncementService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    List<ContestAnnouncement> getAnnouncements(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);
}
