package judgels.jerahmeel.curriculum;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jerahmeel.api.curriculum.CurriculumsResponse;

@Path("/api/v2/curriculums")
public class CurriculumResource {
    @Inject protected CurriculumStore curriculumStore;

    @Inject public CurriculumResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public CurriculumsResponse getCurriculums() {
        return new CurriculumsResponse.Builder()
                .data(curriculumStore.getCurriculums())
                .build();
    }
}
