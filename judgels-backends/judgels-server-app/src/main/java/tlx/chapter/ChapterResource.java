package tlx.chapter;

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
import java.util.List;
import judgels.role.TrainingAdminRoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import tlx.api.chapter.Chapter;
import tlx.api.chapter.ChapterCreateData;
import tlx.api.chapter.ChapterUpdateData;
import tlx.api.chapter.ChaptersResponse;

@Path("/api/v2/chapters")
public class ChapterResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
    @Inject protected ChapterStore chapterStore;

    @Inject public ChapterResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChaptersResponse getChapters(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        List<Chapter> chapters = chapterStore.getChapters();
        return new ChaptersResponse.Builder()
                .data(chapters)
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Chapter createChapter(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ChapterCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return chapterStore.createChapter(data);
    }

    @POST
    @Path("/{chapterJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Chapter updateChapter(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("chapterJid") String chapterJid,
            ChapterUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        return chapterStore.updateChapter(chapterJid, data);
    }
}
