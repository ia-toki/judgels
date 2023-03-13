package judgels.michael.problem.base.editorial;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.net.URI;
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
import judgels.michael.problem.base.BaseProblemResource;
import judgels.michael.resource.EditStatementForm;
import judgels.michael.resource.EditStatementView;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemEditorial;

@Path("/problems/{problemId}/editorials")
public class ProblemEditorialResource extends BaseProblemResource {
    @Inject public ProblemEditorialResource() {}

    @GET
    @Path("")
    @UnitOfWork(readOnly = true)
    public View viewEditorial(
             @Context HttpServletRequest req,
             @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canView(actor, problem));

        if (!problemStore.hasEditorial(actor.getUserJid(), problem.getJid())) {
            CreateEditorialForm form = new CreateEditorialForm();
            form.initialLanguage = "en-US";
            return renderCreateEditorial(actor, problem, form);
        }

        Set<String> enabledLanguages = problemStore.getEditorialEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveEditorialLanguage(req, actor, problem, enabledLanguages);
        ProblemEditorial editorial = problemStore.getEditorial(actor.getUserJid(), problem.getJid(), language);

        HtmlTemplate template = newProblemEditorialTemplate(actor, problem, true);
        template.setActiveSecondaryTab("view");
        return new ViewEditorialView(template, editorial, language, enabledLanguages);
    }

    @POST
    @Path("")
    @UnitOfWork
    public Response postCreateEditorial(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam CreateEditorialForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.initEditorials(actor.getUserJid(), problem.getJid(), form.initialLanguage);

        setCurrentStatementLanguage(req, form.initialLanguage);
        return Response
                .seeOther(URI.create("/problems/" + problemId + "/editorials/edit"))
                .build();
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editEditorial(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = problemStore.getEditorialEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveEditorialLanguage(req, actor, problem, enabledLanguages);
        ProblemEditorial editorial = problemStore.getEditorial(actor.getUserJid(), problem.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.text = editorial.getText();

        return renderEditEditorial(actor, problem, form, language, enabledLanguages);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response postEditEditorial(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = problemStore.getEditorialEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveEditorialLanguage(req, actor, problem, enabledLanguages);

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.updateEditorial(actor.getUserJid(), problem.getJid(), language, new ProblemEditorial.Builder()
                .text(form.text)
                .build());

        return Response
                .seeOther(URI.create("/problems/" + problemId + "/editorials"))
                .build();
    }

    private View renderCreateEditorial(Actor actor, Problem problem, HtmlForm form) {
        HtmlTemplate template = newProblemEditorialTemplate(actor, problem, false);
        return new CreateEditorialView(template, (CreateEditorialForm) form);
    }

    private View renderEditEditorial(Actor actor, Problem problem, HtmlForm form, String language, Set<String> enabledLanguages) {
        HtmlTemplate template = newProblemEditorialTemplate(actor, problem, true);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, (EditStatementForm) form, "/problems", language, enabledLanguages);
    }
}
