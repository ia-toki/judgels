package judgels.sandalphon.api.client.lesson;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.service.api.client.BasicAuthHeader;

@Path("/api/v2/client/lessons")
public interface ClientLessonService {
    @GET
    @Path("/{problemJid}")
    @Produces(APPLICATION_JSON)
    LessonInfo getLesson(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid);

    @POST
    @Path("/allowed-slug-to-jid")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, String> translateAllowedSlugsToJids(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @QueryParam("userJid") String userJid,
            Set<String> slugs);
}
