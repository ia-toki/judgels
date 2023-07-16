package judgels.michael.lesson.version;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.lesson.BaseLessonResource;
import judgels.michael.resource.CommitVersionForm;
import judgels.michael.resource.ListVersionHistoryView;
import judgels.michael.resource.RebaseVersionLocalChangesView;
import judgels.michael.resource.ViewVersionLocalChangesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.lesson.version.LessonVersionStore;

@Path("/lessons/{lessonId}/versions")
public class LessonVersionResource extends BaseLessonResource {
    @Inject protected LessonVersionStore versionStore;

    @Inject public LessonVersionResource() {}

    @GET
    @Path("/local")
    @UnitOfWork(readOnly = true)
    public View viewVersionLocalChanges(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        CommitVersionForm form = new CommitVersionForm();

        return renderViewVersionLocalChanges(actor, lesson, form);
    }

    private View renderViewVersionLocalChanges(Actor actor, Lesson lesson, CommitVersionForm form) {
        boolean isClean = !lessonStore.userCloneExists(actor.getUserJid(), lesson.getJid());

        HtmlTemplate template = newLessonVersionTemplate(actor, lesson);
        template.setActiveSecondaryTab("local");
        return new ViewVersionLocalChangesView(template, form, isClean);
    }

    @POST
    @Path("/local")
    @UnitOfWork
    public Response commitVersionLocalChanges(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam CommitVersionForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        String localChangesError = null;
        if (versionStore.fetchUserClone(actor.getUserJid(), lesson.getJid())) {
            localChangesError = "There have been newer changes in the master copy. Please rebase your local changes.";
        } else if (!versionStore.commitThenMergeUserClone(actor.getUserJid(), lesson.getJid(), form.title, form.description)) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        } else if (!versionStore.pushUserClone(actor.getUserJid(), lesson.getJid())) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        }

        if (localChangesError != null) {
            form.localChangesError = localChangesError;
            return ok(renderViewVersionLocalChanges(actor, lesson, form));
        }

        versionStore.discardUserClone(actor.getUserJid(), lesson.getJid());

        return redirect("/lessons/" + lessonId + "/versions/local");
    }

    @GET
    @Path("/history")
    @UnitOfWork(readOnly = true)
    public View listVersionHistory(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        List<GitCommit> versions = versionStore.getVersions(actor.getUserJid(), lesson.getJid());

        var userJids = Lists.transform(versions, GitCommit::getUserJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        boolean isClean = !lessonStore.userCloneExists(actor.getUserJid(), lesson.getJid());

        HtmlTemplate template = newLessonVersionTemplate(actor, lesson);
        template.setActiveSecondaryTab("history");
        return new ListVersionHistoryView(template, versions, profilesMap, isClean);
    }

    @GET
    @Path("/history/{versionHash}/restore")
    @UnitOfWork
    public Response restoreVersionHistory(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @PathParam("versionHash") String versionHash) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        versionStore.restore(lesson.getJid(), versionHash);

        return redirect("/lessons/" + lessonId + "/versions/history");
    }

    @GET
    @Path("/rebase")
    @UnitOfWork
    public Response rebaseVersionLocalChanges(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        versionStore.fetchUserClone(actor.getUserJid(), lesson.getJid());
        if (!versionStore.updateUserClone(actor.getUserJid(), lesson.getJid())) {
            String localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";

            HtmlTemplate template = newLessonVersionTemplate(actor, lesson);
            template.setActiveSecondaryTab("local");
            return ok(new RebaseVersionLocalChangesView(template, localChangesError));
        }

        return redirect("/lessons/" + lessonId + "/versions/local");
    }

    @GET
    @Path("/discard")
    @UnitOfWork
    public Response discardVersionLocalChanges(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        versionStore.discardUserClone(actor.getUserJid(), lesson.getJid());

        return redirect("/lessons/" + lessonId + "/versions/local");
    }

    private HtmlTemplate newLessonVersionTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("versions");
        template.addSecondaryTab("local", "Local changes", "/lessons/" + lesson.getId() + "/versions/local");
        template.addSecondaryTab("history", "History", "/lessons/" + lesson.getId() + "/versions/history");
        return template;
    }
}
