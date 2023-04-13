package judgels.jerahmeel.api.curriculum;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v2/curriculums")
public interface CurriculumService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    CurriculumsResponse getCurriculums();
}
