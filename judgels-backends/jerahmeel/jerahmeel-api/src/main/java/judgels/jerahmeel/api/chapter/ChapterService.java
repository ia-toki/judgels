package judgels.jerahmeel.api.chapter;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters")
public interface ChapterService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ChaptersResponse getChapters(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Chapter createChapter(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ChapterCreateData data);

    @POST
    @Path("/{chapterJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Chapter updateChapter(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("chapterJid") String chapterJid,
            ChapterUpdateData data);
}
