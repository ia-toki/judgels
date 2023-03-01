package judgels.michael.problem.base;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.profile.ProfileStore;
import judgels.jophiel.user.UserStore;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.SearchProblemsWidget;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.ProblemRoleChecker;
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
    @Inject protected ProblemRoleChecker problemRoleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected BundleProblemStore bundleProblemStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected ProblemSearchStore problemSearchStore;
    @Inject protected ProblemTagStore problemTagStore;
    @Inject protected UserStore userStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("page") @DefaultValue("1") int pageIndex,
            @QueryParam("filter") @DefaultValue("") String filterString,
            @QueryParam("tags") List<String> tags) {

        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Page<Problem> problems = problemSearchStore.searchProblems(pageIndex, "updatedAt", "desc", filterString, tags, actor.getUserJid(), isAdmin);
        Set<String> userJids = problems.getPage().stream().map(Problem::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);
        Map<String, Integer> tagCounts = problemTagStore.getTagCounts(isAdmin);

        HtmlTemplate template = newProblemsTemplate(actor);
        template.setTitle("Problems");
        if (isWriter) {
            template.addMainButton("Create", "/problems/new");
        }
        template.setSearchProblemsWidget(new SearchProblemsWidget(pageIndex, filterString, tags, tagCounts));
        return new ListProblemsView(template, problems, filterString, profilesMap, tags);
    }

    @GET
    @Path("/new")
    @UnitOfWork(readOnly = true)
    public View createProblem(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        CreateProblemForm form = new CreateProblemForm();
        form.gradingEngine = "Batch";
        form.initialLanguage = "en-US";

        return renderCreateProblem(actor, form);
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

    @GET
    @Path("/{problemId}")
    @UnitOfWork(readOnly = true)
    public View viewProblem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canView(actor, problem));

        Profile profile = profileStore.getProfile(Instant.now(), problem.getAuthorJid());

        Map<ProblemSetterRole, List<String>> setters = problemStore.findProblemSettersByProblemJid(problem.getJid());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), setters.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet()));

        String writerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.WRITER), profilesMap);
        String developerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.DEVELOPER), profilesMap);
        String testerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.TESTER), profilesMap);
        String editorialistUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.EDITORIALIST), profilesMap);

        List<String> tags = problemTagStore.findTopicTags(problem.getJid()).stream().sorted().collect(Collectors.toList());

        HtmlTemplate template = newProblemGeneralTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ViewProblemView(template, problem, profile, writerUsernames, developerUsernames, testerUsernames, editorialistUsernames, tags);
    }

    @GET
    @Path("/{problemId}/edit")
    @UnitOfWork(readOnly = true)
    public View editProblem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Map<ProblemSetterRole, List<String>> setters = problemStore.findProblemSettersByProblemJid(problem.getJid());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), setters.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet()));

        EditProblemForm form = new EditProblemForm();
        form.slug = problem.getSlug();
        form.additionalNote = problem.getAdditionalNote();
        form.writerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.WRITER), profilesMap);
        form.developerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.DEVELOPER), profilesMap);
        form.testerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.TESTER), profilesMap);
        form.editorialistUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.EDITORIALIST), profilesMap);
        form.tags = problemTagStore.findTopicTags(problem.getJid());

        return renderEditProblem(actor, problem, form);
    }

    @POST
    @Path("/{problemId}/edit")
    @UnitOfWork
    public Response postEditProblem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditProblemForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (!problem.getSlug().equals(form.slug) && problemStore.problemExistsBySlug(form.slug)) {
            return Response.ok(renderEditProblem(actor, problem, form.withGlobalError("Slug already exists."))).build();
        }

        problemStore.updateProblem(problem.getJid(), form.slug, form.additionalNote);

        Set<String> usernames = new HashSet<>();
        usernames.addAll(Arrays.asList(form.writerUsernames.split(",")));
        usernames.addAll(Arrays.asList(form.developerUsernames.split(",")));
        usernames.addAll(Arrays.asList(form.testerUsernames.split(",")));
        usernames.addAll(Arrays.asList(form.editorialistUsernames.split(",")));
        Map<String, String> jidsMap = userStore.translateUsernamesToJids(usernames);

        Map<ProblemSetterRole, List<String>> setters = problemStore.findProblemSettersByProblemJid(problem.getJid());
        updateProblemSetters(problem.getJid(), ProblemSetterRole.WRITER, form.writerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.DEVELOPER, form.developerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.TESTER, form.testerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.EDITORIALIST, form.editorialistUsernames, setters, jidsMap);

        problemTagStore.updateTopicTags(problem.getJid(), form.tags);

        return Response
                .seeOther(URI.create("/problems/" + problem.getId()))
                .build();
    }

    private String userJidsToUsernames(List<String> userJids, Map<String, Profile> profilesMap) {
        if (userJids == null) {
            return "";
        }
        return userJids.stream()
                .filter(profilesMap::containsKey)
                .map(profilesMap::get)
                .map(Profile::getUsername)
                .collect(Collectors.joining(","));
    }

    private List<String> usernamesToUserJids(String usernames, Map<String, String> jidsMap) {
        if (usernames == null) {
            return ImmutableList.of();
        }
        return Lists.newArrayList(usernames.split(","))
                .stream()
                .filter(jidsMap::containsKey)
                .map(jidsMap::get)
                .collect(Collectors.toList());
    }

    private void updateProblemSetters(
            String problemJid,
            ProblemSetterRole role,
            String usernames,
            Map<ProblemSetterRole, List<String>> setters,
            Map<String, String> jidsMap) {

        List<String> userJids = usernamesToUserJids(usernames, jidsMap);
        if (!userJids.equals(setters.getOrDefault(role, ImmutableList.of()))) {
            problemStore.updateProblemSettersByProblemJidAndRole(problemJid, role, userJids);
        }
    }

    private HtmlTemplate newProblemGeneralTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("general");
        template.addSecondaryTab("view", "View", "/problems/" + problem.getId());
        template.addSecondaryTab("edit", "Edit", "/problems/" + problem.getId() + "/edit");
        return template;
    }

    private View renderCreateProblem(Actor actor, HtmlForm form) {
        HtmlTemplate template = newProblemsTemplate(actor);
        template.setTitle("New problem");
        return new CreateProblemView(template, (CreateProblemForm) form);
    }

    private View renderEditProblem(Actor actor, Problem problem, HtmlForm form) {
        HtmlTemplate template = newProblemGeneralTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditProblemView(template, (EditProblemForm) form);
    }
}
