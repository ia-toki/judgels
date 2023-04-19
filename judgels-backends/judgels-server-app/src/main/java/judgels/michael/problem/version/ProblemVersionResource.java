package judgels.michael.problem.version;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
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
import judgels.michael.problem.BaseProblemResource;
import judgels.michael.resource.CommitVersionForm;
import judgels.michael.resource.ListVersionHistoryView;
import judgels.michael.resource.RebaseVersionLocalChangesView;
import judgels.michael.resource.ViewVersionLocalChangesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import judgels.sandalphon.problem.base.version.ProblemVersionStore;

@Path("/problems/{problemId}/versions")
public class  ProblemVersionResource extends BaseProblemResource {
    @Inject protected ProblemVersionStore versionStore;
    @Inject protected ProblemTagStore tagStore;

    @Inject public ProblemVersionResource() {}

    @GET
    @Path("/local")
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
    @Path("/local")
    @UnitOfWork
    public Response commitVersionLocalChanges(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam CommitVersionForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        String localChangesError = null;
        if (versionStore.fetchUserClone(actor.getUserJid(), problem.getJid())) {
            localChangesError = "There have been newer changes in the master copy. Please rebase your local changes.";
        } else if (!versionStore.commitThenMergeUserClone(actor.getUserJid(), problem.getJid(), form.title, form.description)) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        } else if (!versionStore.pushUserClone(actor.getUserJid(), problem.getJid())) {
            localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        }

        if (localChangesError != null) {
            form.localChangesError = localChangesError;
            return ok(renderViewVersionLocalChanges(actor, problem, form));
        }

        versionStore.discardUserClone(actor.getUserJid(), problem.getJid());
        tagStore.refreshDerivedTags(problem.getJid());

        return redirect("/problems/" + problemId + "/versions/local");
    }

    @GET
    @Path("/history")
    @UnitOfWork(readOnly = true)
    public View listVersionHistory(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        List<GitCommit> versions = versionStore.getVersions(actor.getUserJid(), problem.getJid());

        Set<String> userJids = versions.stream().map(GitCommit::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

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

        versionStore.restore(problem.getJid(), versionHash);

        return redirect("/problems/" + problemId + "/versions/history");
    }

    @GET
    @Path("/rebase")
    @UnitOfWork
    public Response rebaseVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        versionStore.fetchUserClone(actor.getUserJid(), problem.getJid());
        if (!versionStore.updateUserClone(actor.getUserJid(), problem.getJid())) {
            String localChangesError = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";

            HtmlTemplate template = newProblemVersionTemplate(actor, problem);
            template.setActiveSecondaryTab("local");
            return ok(new RebaseVersionLocalChangesView(template, localChangesError));
        }

        return redirect("/problems/" + problemId + "/versions/local");
    }

    @GET
    @Path("/discard")
    @UnitOfWork
    public Response discardVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        versionStore.discardUserClone(actor.getUserJid(), problem.getJid());

        return redirect("/problems/" + problemId + "/versions/local");
    }

    private HtmlTemplate newProblemVersionTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("versions");
        template.addSecondaryTab("local", "Local changes", "/problems/" + problem.getId() + "/versions/local");
        template.addSecondaryTab("history", "History", "/problems/" + problem.getId() + "/versions/history");
        return template;
    }
}
