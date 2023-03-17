package judgels.michael.lesson.version;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

@Path("/lessons/{lessonId}/versions")
public class LessonVersionResource extends BaseLessonResource {
    @Inject public LessonVersionResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewVersionLocalChanges(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

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
    @UnitOfWork
    public Response commitVersionLocalChanges(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam CommitVersionForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        String localChangesError = null;
        if (lessonStore.fetchUserClone(actor.getUserJid(), lesson.getJid())) {
            localChangesError = "Your local changes conflict with the master copy. Please rebase your local changes.";
        } else if (!lessonStore.commitThenMergeUserClone(actor.getUserJid(), lesson.getJid(), form.title, form.description)) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        } else if (!lessonStore.pushUserClone(actor.getUserJid(), lesson.getJid())) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        }

        if (localChangesError != null) {
            form.localChangesError = localChangesError;
            return ok(renderViewVersionLocalChanges(actor, lesson, form));
        }

        lessonStore.discardUserClone(actor.getUserJid(), lesson.getJid());

        return redirect("/lessons/" + lessonId + "/versions");
    }

    @GET
    @Path("/history")
    @UnitOfWork(readOnly = true)
    public View listVersionHistory(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        List<GitCommit> versions = lessonStore.getVersions(actor.getUserJid(), lesson.getJid());

        Set<String> userJids = versions.stream().map(GitCommit::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

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
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        lessonStore.restore(lesson.getJid(), versionHash);

        return redirect("/lessons/" + lessonId + "/versions/history");
    }

    @GET
    @Path("/rebase")
    @UnitOfWork
    public Response rebaseVersionLocalChanges(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        lessonStore.fetchUserClone(actor.getUserJid(), lesson.getJid());
        if (!lessonStore.updateUserClone(actor.getUserJid(), lesson.getJid())) {
            HtmlTemplate template = newLessonVersionTemplate(actor, lesson);
            template.setActiveSecondaryTab("local");
            return ok(new RebaseVersionLocalChangesView(
                    template,
                    "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.",
                    "/lessons/" + lessonId + "/versions"));
        }

        return redirect("/lessons/" + lessonId + "/versions");
    }

    @GET
    @Path("/discard")
    @UnitOfWork
    public Response discardVersionLocalChanges(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        lessonStore.discardUserClone(actor.getUserJid(), lesson.getJid());

        return redirect("/lessons/" + lessonId + "/versions");
    }

    private HtmlTemplate newLessonVersionTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("versions");
        template.addSecondaryTab("local", "Local changes", "/lessons/" + lesson.getId() + "/versions");
        template.addSecondaryTab("history", "History", "/lessons/" + lesson.getId() + "/versions/history");
        return template;
    }
}
