package judgels.michael.problem.base.version;

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
import judgels.michael.problem.base.BaseProblemResource;
import judgels.michael.resource.CommitVersionForm;
import judgels.michael.resource.ListVersionHistoryView;
import judgels.michael.resource.RebaseVersionLocalChangesView;
import judgels.michael.resource.ViewVersionLocalChangesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;

@Path("/problems/{problemId}/versions")
public class ProblemVersionResource extends BaseProblemResource {
    @Inject protected ProblemTagStore tagStore;

    @Inject public ProblemVersionResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        CommitVersionForm form = new CommitVersionForm();

        return renderViewVersionLocalChanges(actor, problem, form);
    }

    private View renderViewVersionLocalChanges(Actor actor, Problem problem, CommitVersionForm form) {
        boolean isClean = !problemStore.userCloneExists(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemVersionTemplate(actor, problem);
        template.setActiveSecondaryTab("local");
        return new ViewVersionLocalChangesView(template, form, isClean);
    }

    @POST
    @UnitOfWork
    public Response commitVersionLocalChanges(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam CommitVersionForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        String localChangesError = null;
        if (problemStore.fetchUserClone(actor.getUserJid(), problem.getJid())) {
            localChangesError = "Your local changes conflict with the master copy. Please rebase your local changes.";
        } else if (!problemStore.commitThenMergeUserClone(actor.getUserJid(), problem.getJid(), form.title, form.description)) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        } else if (!problemStore.pushUserClone(actor.getUserJid(), problem.getJid())) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        }

        if (localChangesError != null) {
            form.localChangesError = localChangesError;
            return ok(renderViewVersionLocalChanges(actor, problem, form));
        }

        problemStore.discardUserClone(actor.getUserJid(), problem.getJid());
        tagStore.refreshDerivedTags(problem.getJid());

        return redirect("/problems/" + problemId + "/versions");
    }

    @GET
    @Path("/history")
    @UnitOfWork(readOnly = true)
    public View listVersionHistory(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        List<GitCommit> versions = problemStore.getVersions(actor.getUserJid(), problem.getJid());

        Set<String> userJids = versions.stream().map(GitCommit::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        boolean isClean = !problemStore.userCloneExists(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemVersionTemplate(actor, problem);
        template.setActiveSecondaryTab("history");
        return new ListVersionHistoryView(template, versions, profilesMap, isClean);
    }

    @GET
    @Path("/history/{versionHash}/restore")
    @UnitOfWork
    public Response restoreVersionHistory(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("versionHash") String versionHash) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.restore(problem.getJid(), versionHash);

        return redirect("/problems/" + problemId + "/versions/history");
    }

    @GET
    @Path("/rebase")
    @UnitOfWork
    public Response rebaseVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.fetchUserClone(actor.getUserJid(), problem.getJid());
        if (!problemStore.updateUserClone(actor.getUserJid(), problem.getJid())) {
            HtmlTemplate template = newProblemVersionTemplate(actor, problem);
            template.setActiveSecondaryTab("local");
            return ok(new RebaseVersionLocalChangesView(
                    template,
                    "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.",
                    "/problems/" + problemId + "/versions"));
        }

        return redirect("/problems/" + problemId + "/versions");
    }

    @GET
    @Path("/discard")
    @UnitOfWork
    public Response discardVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.discardUserClone(actor.getUserJid(), problem.getJid());

        return redirect("/problems/" + problemId + "/versions");
    }

    private HtmlTemplate newProblemVersionTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("versions");
        template.addSecondaryTab("local", "Local changes", "/problems/" + problem.getId() + "/versions");
        template.addSecondaryTab("history", "History", "/problems/" + problem.getId() + "/versions/history");
        return template;
    }
}
