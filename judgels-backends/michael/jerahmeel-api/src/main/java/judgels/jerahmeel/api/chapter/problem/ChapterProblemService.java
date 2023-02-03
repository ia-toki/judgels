package judgels.jerahmeel.api.chapter.problem;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/{chapterJid}/problems")
public interface ChapterProblemService {
    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void setProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("chapterJid") String chapterJid,
            List<ChapterProblemData> data);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ChapterProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid);

    @GET
    @Path("/{problemAlias}/worksheet")
    @Produces(APPLICATION_JSON)
    ChapterProblemWorksheet getProblemWorksheet(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language);
}
