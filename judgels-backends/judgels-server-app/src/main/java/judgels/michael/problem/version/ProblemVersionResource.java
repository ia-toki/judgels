package judgels.michael.problem.version;

import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemErrors;
import judgels.api.profile.Profile;
import judgels.catalog.problem.version.ProblemVersionService;
import judgels.catalog.problem.version.ProblemVersionStore;
import judgels.core.api.JudgelsApiException;
import judgels.core.git.GitCommit;
import judgels.michael.problem.BaseProblemResource;
import judgels.michael.resource.CommitVersionForm;
import judgels.michael.resource.ListVersionHistoryView;
import judgels.michael.resource.RebaseVersionLocalChangesView;
import judgels.michael.resource.ViewVersionLocalChangesView;
import judgels.michael.template.HtmlTemplate;
import judgels.session.Actor;

@Path("/problems/{problemId}/versions")
public class ProblemVersionResource extends BaseProblemResource {
    private static final String LOCAL_CHANGES_CONFLICT_ERROR =
            "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";

    @Inject protected ProblemVersionStore versionStore;
    @Inject protected ProblemVersionService versionService;

    @Inject public ProblemVersionResource() {}

    @GET
    @Path("/local")
    @UnitOfWork(readOnly = true)
    public View viewVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        try {
            versionService.commitLocalChanges(actor.getUserJid(), problem.getJid(), form.title, form.description);
        } catch (JudgelsApiException e) {
            form.localChangesError = e.getMessage().equals(ProblemErrors.VERSION_LOCAL_CHANGES_OUTDATED)
                    ? "There have been newer changes in the master copy. Please rebase your local changes."
                    : LOCAL_CHANGES_CONFLICT_ERROR;
            return ok(renderViewVersionLocalChanges(actor, problem, form));
        }

        return redirect("/problems/" + problemId + "/versions/local");
    }

    @GET
    @Path("/history")
    @UnitOfWork(readOnly = true)
    public View listVersionHistory(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        List<GitCommit> versions = versionStore.getVersions(actor.getUserJid(), problem.getJid());

        var userJids = Lists.transform(versions, GitCommit::getUserJid);
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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        versionService.restoreVersion(actor.getUserJid(), problem.getJid(), versionHash);

        return redirect("/problems/" + problemId + "/versions/history");
    }

    @GET
    @Path("/rebase")
    @UnitOfWork
    public Response rebaseVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        try {
            versionService.rebaseLocalChanges(actor.getUserJid(), problem.getJid());
        } catch (JudgelsApiException e) {
            HtmlTemplate template = newProblemVersionTemplate(actor, problem);
            template.setActiveSecondaryTab("local");
            return ok(new RebaseVersionLocalChangesView(template, LOCAL_CHANGES_CONFLICT_ERROR));
        }

        return redirect("/problems/" + problemId + "/versions/local");
    }

    @GET
    @Path("/discard")
    @UnitOfWork
    public Response discardVersionLocalChanges(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        versionService.discardLocalChanges(actor.getUserJid(), problem.getJid());

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
