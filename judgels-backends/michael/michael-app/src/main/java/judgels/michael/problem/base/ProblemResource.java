package judgels.michael.problem.base;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.profile.ProfileStore;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.SearchProblemsWidget;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.ProblemSearchStore;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import judgels.sandalphon.problem.bundle.BundleProblemStore;
import judgels.sandalphon.problem.bundle.statement.BundleProblemStatementUtils;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import judgels.sandalphon.problem.programming.statement.ProgrammingProblemStatementUtils;
import judgels.sandalphon.role.RoleChecker;

@Path("/problems")
public class ProblemResource extends BaseProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected BundleProblemStore bundleProblemStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected ProblemSearchStore problemSearchStore;
    @Inject protected ProblemTagStore problemTagStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("pageIndex") @DefaultValue("1") int pageIndex,
            @QueryParam("filterString") @DefaultValue("") String filterString) {

        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Page<Problem> problems = problemSearchStore.searchProblems(pageIndex, "updatedAt", "desc", filterString, null, actor.getUserJid(), isAdmin);
        Set<String> userJids = problems.getPage().stream().map(Problem::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newTemplate(actor);
        template.setTitle("Problems");
        if (isWriter) {
            template.addMainButton("Create", "/problems/new");
        }
        template.setSearchProblemsWidget(new SearchProblemsWidget(pageIndex, filterString));
        return new ListProblemsView(template, problems, filterString, profilesMap);
    }

    @GET
    @Path("/new")
    @UnitOfWork(readOnly = true)
    public View createProblem(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        return renderCreateProblem(actor, new CreateProblemForm());
    }

    @POST
    @Path("/new")
    @UnitOfWork
    public Response postCreateProblem(@Context HttpServletRequest req, @BeanParam CreateProblemForm form) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        if (problemStore.problemExistsBySlug(form.slug)) {
            return Response.ok(renderCreateProblem(actor, form.withGlobalError("Slug already exists."))).build();
        }

        ProblemType type;
        String initialText;
        if (form.gradingEngine.equals("Bundle")) {
            type = ProblemType.BUNDLE;
            initialText = BundleProblemStatementUtils.getDefaultStatement(form.initialLanguage);
        } else {
            type = ProblemType.PROGRAMMING;
            initialText = ProgrammingProblemStatementUtils.getDefaultText(form.initialLanguage);
        }

        Problem problem = problemStore.createProblem(type, form.slug, form.additionalNote, form.initialLanguage);
        problemStore.updateStatement(null, problem.getJid(), form.initialLanguage, new ProblemStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(form.initialLanguage))
                .text(initialText)
                .build());

        if (type == ProblemType.BUNDLE) {
            bundleProblemStore.initBundleProblem(problem.getJid());
        } else {
            programmingProblemStore.initProgrammingProblem(problem.getJid(), form.gradingEngine);
            problemTagStore.refreshDerivedTags(problem.getJid());
        }

        problemStore.initRepository(actor.getUserJid(), problem.getJid());

        return Response
                .seeOther(URI.create("/problems/" + problem.getId()))
                .build();
    }

    private View renderCreateProblem(Actor actor, HtmlForm form) {
        HtmlTemplate template = newTemplate(actor);
        template.setTitle("Create problem");
        return new CreateProblemView(template, (CreateProblemForm) form);
    }
}
