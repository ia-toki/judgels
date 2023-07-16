package judgels.michael.problem;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.SearchProblemsWidget;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import judgels.sandalphon.problem.bundle.BundleProblemStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;

@Path("/problems")
public class ProblemResource extends BaseProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected BundleProblemStore bundleProblemStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected ProblemTagStore tagStore;

    @Inject public ProblemResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("term") @DefaultValue("") String termFilter,
            @QueryParam("tags") Set<String> tagsFilter) {

        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actor.getUserJid());
        Page<Problem> problems = problemStore.getProblems(userJid, termFilter, tagsFilter, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(problems.getPage(), Problem::getAuthorJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);
        Map<String, Integer> tagCounts = tagStore.getTagCounts(isAdmin);

        HtmlTemplate template = newProblemsTemplate(actor);
        template.setTitle("Problems");
        if (isWriter) {
            template.addMainButton("New problem", "/problems/new");
        }
        template.setSearchProblemsWidget(new SearchProblemsWidget(pageNumber, termFilter, tagsFilter, tagCounts));
        return new ListProblemsView(template, problems, termFilter, tagsFilter, profilesMap);
    }

    @GET
    @Path("/new")
    @UnitOfWork(readOnly = true)
    public View newProblem(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        NewProblemForm form = new NewProblemForm();
        form.gradingEngine = "Batch";
        form.initialLanguage = "en-US";

        return renderNewProblem(actor, form);
    }

    private View renderNewProblem(Actor actor, NewProblemForm form) {
        HtmlTemplate template = newProblemsTemplate(actor);
        template.setTitle("New problem");
        return new NewProblemView(template, form);
    }

    @POST
    @Path("/new")
    @UnitOfWork
    public Response createProblem(@Context HttpServletRequest req, @BeanParam NewProblemForm form) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        if (problemStore.problemExistsBySlug(form.slug)) {
            form.globalError = "Slug already exists.";
            return ok(renderNewProblem(actor, form));
        }

        ProblemType type = form.gradingEngine.equals("Bundle") ? ProblemType.BUNDLE : ProblemType.PROGRAMMING;
        Problem problem = problemStore.createProblem(type, form.slug, form.additionalNote);

        statementStore.initStatements(problem.getJid(), type, form.initialLanguage);

        if (type == ProblemType.BUNDLE) {
            bundleProblemStore.initBundleProblem(problem.getJid());
        } else {
            programmingProblemStore.initProgrammingProblem(problem.getJid(), form.gradingEngine);
            tagStore.refreshDerivedTags(problem.getJid());
        }

        problemStore.initRepository(actor.getUserJid(), problem.getJid());

        setCurrentStatementLanguage(req, form.initialLanguage);
        return redirect("/problems/" + problem.getType().name().toLowerCase() + "/" + problem.getId() + "/statements");
    }

    @GET
    @Path("/{problemId}")
    @UnitOfWork(readOnly = true)
    public View viewProblem(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Profile profile = profileStore.getProfile(problem.getAuthorJid());

        Map<ProblemSetterRole, List<String>> setters = problemStore.getProblemSetters(problem.getJid());
        Map<String, Profile> profilesMap = profileStore.getProfiles(setters.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet()));

        String writerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.WRITER), profilesMap);
        String developerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.DEVELOPER), profilesMap);
        String testerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.TESTER), profilesMap);
        String editorialistUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.EDITORIALIST), profilesMap);

        List<String> tags = tagStore.findTopicTags(problem.getJid()).stream().sorted().collect(Collectors.toList());

        HtmlTemplate template = newProblemGeneralTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ViewProblemView(template, problem, profile, writerUsernames, developerUsernames, testerUsernames, editorialistUsernames, tags);
    }

    @GET
    @Path("/{problemId}/edit")
    @UnitOfWork(readOnly = true)
    public View editProblem(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        Map<ProblemSetterRole, List<String>> setters = problemStore.getProblemSetters(problem.getJid());
        Map<String, Profile> profilesMap = profileStore.getProfiles(setters.values()
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
        form.tags = tagStore.findTopicTags(problem.getJid());

        return renderEditProblem(actor, problem, form);
    }

    private View renderEditProblem(Actor actor, Problem problem, EditProblemForm form) {
        HtmlTemplate template = newProblemGeneralTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditProblemView(template, form);
    }

    @POST
    @Path("/{problemId}/edit")
    @UnitOfWork
    public Response updateProblem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditProblemForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!problem.getSlug().equals(form.slug) && problemStore.problemExistsBySlug(form.slug)) {
            form.globalError = "Slug already exists.";
            return ok(renderEditProblem(actor, problem, form));
        }

        problemStore.updateProblem(problem.getJid(), form.slug, form.additionalNote);

        Set<String> usernames = new HashSet<>();
        usernames.addAll(Arrays.asList(form.writerUsernames.split(",")));
        usernames.addAll(Arrays.asList(form.developerUsernames.split(",")));
        usernames.addAll(Arrays.asList(form.testerUsernames.split(",")));
        usernames.addAll(Arrays.asList(form.editorialistUsernames.split(",")));
        Map<String, String> jidsMap = userStore.translateUsernamesToJids(usernames);

        Map<ProblemSetterRole, List<String>> setters = problemStore.getProblemSetters(problem.getJid());
        updateProblemSetters(problem.getJid(), ProblemSetterRole.WRITER, form.writerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.DEVELOPER, form.developerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.TESTER, form.testerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.EDITORIALIST, form.editorialistUsernames, setters, jidsMap);

        tagStore.updateTopicTags(problem.getJid(), form.tags);

        return redirect("/problems/" + problemId);
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
            problemStore.updateProblemSetters(problemJid, role, userJids);
        }
    }

    private HtmlTemplate newProblemGeneralTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("general");
        template.addSecondaryTab("view", "View", "/problems/" + problem.getId());
        if (roleChecker.canEdit(actor, problem)) {
            template.addSecondaryTab("edit", "Edit", "/problems/" + problem.getId() + "/edit");
        }
        return template;
    }
}
