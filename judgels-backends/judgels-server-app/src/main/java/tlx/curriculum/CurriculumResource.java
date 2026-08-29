package tlx.curriculum;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import tlx.api.curriculum.Curriculum;
import tlx.api.curriculum.CurriculumUpdateData;
import tlx.api.curriculum.CurriculumsResponse;
import tlx.role.TrainingAdminRoleChecker;

@Path("/api/v2/curriculums")
public class CurriculumResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
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

    @POST
    @Path("/{curriculumJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Curriculum updateCurriculum(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("curriculumJid") String curriculumJid,
            CurriculumUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(curriculumStore.getCurriculumByJid(curriculumJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        return curriculumStore.updateCurriculum(curriculumJid, data);
    }
}
