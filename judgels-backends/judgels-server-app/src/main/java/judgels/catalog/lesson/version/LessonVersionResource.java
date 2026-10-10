package judgels.catalog.lesson.version;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import judgels.api.catalog.CatalogVersion;
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.lesson.version.LessonVersionCommitData;
import judgels.api.catalog.lesson.version.LessonVersionsResponse;
import judgels.catalog.CatalogVersions;
import judgels.catalog.lesson.LessonRoleChecker;
import judgels.catalog.lesson.LessonStore;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;

@Path("/api/v4/lessons/{lessonJid}/versions")
public class LessonVersionResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected LessonRoleChecker roleChecker;
    @Inject protected LessonStore lessonStore;
    @Inject protected LessonVersionStore versionStore;
    @Inject protected LessonVersionService versionService;
    @Inject protected ProfileStore profileStore;

    @Inject public LessonVersionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LessonVersionsResponse getVersions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        checkCanEdit(authHeader, lessonJid);

        List<CatalogVersion> versions = CatalogVersions.fromGitCommits(versionStore.getVersions(null, lessonJid));

        var userJids = Lists.transform(versions, CatalogVersion::getUserJid);

        return new LessonVersionsResponse.Builder()
                .data(versions)
                .profilesMap(profileStore.getProfiles(userJids))
                .build();
    }

    @POST
    @Path("/{versionHash}/restore")
    @UnitOfWork
    public void restoreVersion(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("versionHash") String versionHash) {

        String actorJid = checkCanEdit(authHeader, lessonJid);

        versionService.restoreVersion(actorJid, lessonJid, versionHash);
    }

    @POST
    @Path("/local/commit")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void commitVersionLocalChanges(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            LessonVersionCommitData data) {

        String actorJid = checkCanEdit(authHeader, lessonJid);

        versionService.commitLocalChanges(actorJid, lessonJid, data.getTitle(), data.getDescription());
    }

    @POST
    @Path("/local/rebase")
    @UnitOfWork
    public void rebaseVersionLocalChanges(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        String actorJid = checkCanEdit(authHeader, lessonJid);

        versionService.rebaseLocalChanges(actorJid, lessonJid);
    }

    @DELETE
    @Path("/local")
    @UnitOfWork
    public void discardVersionLocalChanges(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        String actorJid = checkCanEdit(authHeader, lessonJid);

        versionService.discardLocalChanges(actorJid, lessonJid);
    }

    // Unlike the endpoints that write a lesson's files, these act on the actor's clone as it is,
    // so they must not create one.
    private String checkCanEdit(AuthHeader authHeader, String lessonJid) {
        String actorJid = actorChecker.check(authHeader);
        Lesson lesson = checkFound(lessonStore.getLessonByJid(lessonJid));
        checkAllowed(roleChecker.canEdit(actorJid, lesson));
        return actorJid;
    }
}
