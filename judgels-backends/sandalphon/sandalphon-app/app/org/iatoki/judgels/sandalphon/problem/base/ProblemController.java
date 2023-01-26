package org.iatoki.judgels.sandalphon.problem.base;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.ProblemSearchStore;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.html.createProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.editProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.listProblemsView;
import org.iatoki.judgels.sandalphon.problem.base.html.searchProblemsView;
import org.iatoki.judgels.sandalphon.problem.base.html.viewProblemView;
import org.iatoki.judgels.sandalphon.role.RoleChecker;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProblemController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemTagStore problemTagStore;
    private final ProblemSearchStore problemSearchStore;
    private final RoleChecker roleChecker;
    private final ProblemRoleChecker problemRoleChecker;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public ProblemController(
            ProblemStore problemStore,
            ProblemTagStore problemTagStore,
            ProblemSearchStore problemSearchStore,
            RoleChecker roleChecker,
            ProblemRoleChecker problemRoleChecker,
            UserSearchService userSearchService,
            ProfileService profileService) {

        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.problemTagStore = problemTagStore;
        this.problemSearchStore = problemSearchStore;
        this.roleChecker = roleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.userSearchService = userSearchService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result index(Http.Request req) {
        return listProblems(req, 1, "updatedAt", "desc", "", null);
    }

    @Transactional(readOnly = true)
    public Result listProblems(Http.Request req, long pageIndex, String sortBy, String orderBy, String filterString, List<String> tags) {
        String actorJid = getUserJid(req);
        boolean isAdmin = roleChecker.isAdmin(req);
        boolean isWriter = roleChecker.isWriter(req);

        Page<Problem> problems = problemSearchStore.searchProblems(pageIndex, sortBy, orderBy, filterString, tags, actorJid, isAdmin);

        Set<String> userJids = problems.getPage().stream().map(Problem::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);
        Map<String, Integer> tagCounts = problemTagStore.getTagCounts(isAdmin);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listProblemsView.render(problems, profilesMap, sortBy, orderBy, filterString, tags));
        if (isWriter) {
            template.addMainButton("Create", routes.ProblemController.createProblem());
        }
        template.addLowerSidebarWidget(searchProblemsView.render(pageIndex, sortBy, orderBy, filterString, tags, tagCounts));
        template.setMainTitle("Problems");
        template.setPageTitle("Problems");
        return renderTemplate(template);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createProblem(Http.Request req) {
        Form<ProblemCreateForm> form = formFactory.form(ProblemCreateForm.class);

        return showCreateProblem(req, form);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProblem(Http.Request req) {
        Form<ProblemCreateForm> form = formFactory.form(ProblemCreateForm.class).bindFromRequest(req);

        if (formHasErrors(form)) {
            return showCreateProblem(req, form);
        }

        if (problemStore.problemExistsBySlug(form.get().slug)) {
            return showCreateProblem(req, form.withError("slug", "Slug already exists"));
        }

        ProblemCreateForm data = form.get();
        Map<String, String> justCreatedProblem = newJustCreatedProblem(data.slug, data.additionalNote, data.initLanguageCode);

        if (data.type.equals(ProblemType.PROGRAMMING.name())) {
            return redirect(org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.createProgrammingProblem())
                    .addingToSession(req, justCreatedProblem);
        } else if (data.type.equals(ProblemType.BUNDLE.name())) {
            return redirect(org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.createBundleProblem())
                    .addingToSession(req, justCreatedProblem);
        }

        return internalServerError();
    }

    public Result enterProblem(long problemId) {
        return redirect(routes.ProblemController.jumpToStatement(problemId));
    }

    public Result jumpToStatement(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
    }

    public Result jumpToEditorial(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.editorial.routes.ProblemEditorialController.viewEditorial(problemId));
    }

    public Result jumpToVersions(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.version.routes.ProblemVersionController.viewVersionLocalChanges(problemId));
    }

    public Result jumpToPartners(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problemId));
    }

    @Transactional(readOnly = true)
    public Result viewProblem(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToViewStatement(req, problem, language));

        Profile profile = profileService.getProfile(problem.getAuthorJid());

        Map<ProblemSetterRole, List<String>> setters = problemStore.findProblemSettersByProblemJid(problem.getJid());
        Map<String, Profile> profilesMap = profileService.getProfiles(setters.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet()));

        String writerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.WRITER), profilesMap);
        String developerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.DEVELOPER), profilesMap);
        String testerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.TESTER), profilesMap);
        String editorialistUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.EDITORIALIST), profilesMap);

        List<String> tags = problemTagStore.findTopicTags(problem.getJid()).stream().sorted().collect(Collectors.toList());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewProblemView.render(problem, profile, writerUsernames, developerUsernames, testerUsernames, editorialistUsernames, tags));
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.markBreadcrumbLocation("View problem", routes.ProblemController.viewProblem(problem.getId()));
        template.setPageTitle("Problem - View");
        return renderProblemTemplate(template, problem);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editProblem(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToUpdateStatement(req, problem));

        Map<ProblemSetterRole, List<String>> setters = problemStore.findProblemSettersByProblemJid(problem.getJid());
        Map<String, Profile> profilesMap = profileService.getProfiles(setters.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet()));

        Set<String> tags = problemTagStore.findTopicTags(problem.getJid());

        ProblemEditForm data = new ProblemEditForm();
        data.slug = problem.getSlug();
        data.additionalNote = problem.getAdditionalNote();
        data.writerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.WRITER), profilesMap);
        data.developerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.DEVELOPER), profilesMap);
        data.testerUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.TESTER), profilesMap);
        data.editorialistUsernames = userJidsToUsernames(setters.get(ProblemSetterRole.EDITORIALIST), profilesMap);
        data.tags = tags.stream().collect(Collectors.toMap(e -> e, e -> e));

        Form<ProblemEditForm> form = formFactory.form(ProblemEditForm.class).fill(data);

        return showEditProblem(req, form, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditProblem(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToUpdateStatement(req, problem));

        Form<ProblemEditForm> form = formFactory.form(ProblemEditForm.class).bindFromRequest(req);

        if (formHasErrors(form)) {
            return showEditProblem(req, form, problem);
        }

        if (!problem.getSlug().equals(form.get().slug) && problemStore.problemExistsBySlug(form.get().slug)) {
            return showEditProblem(req, form.withError("slug", "Slug already exists"), problem);
        }

        ProblemEditForm data = form.get();

        Set<String> usernames = new HashSet<>();
        usernames.addAll(ImmutableSet.copyOf(Optional.ofNullable(data.writerUsernames).orElse("").split(",")));
        usernames.addAll(ImmutableSet.copyOf(Optional.ofNullable(data.developerUsernames).orElse("").split(",")));
        usernames.addAll(ImmutableSet.copyOf(Optional.ofNullable(data.testerUsernames).orElse("").split(",")));
        usernames.addAll(ImmutableSet.copyOf(Optional.ofNullable(data.editorialistUsernames).orElse("").split(",")));
        Map<String, String> jidsMap = userSearchService.translateUsernamesToJids(usernames);

        problemStore.updateProblem(problem.getJid(), data.slug, data.additionalNote);

        Map<ProblemSetterRole, List<String>> setters = problemStore.findProblemSettersByProblemJid(problem.getJid());
        updateProblemSetters(problem.getJid(), ProblemSetterRole.WRITER, data.writerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.DEVELOPER, data.developerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.TESTER, data.testerUsernames, setters, jidsMap);
        updateProblemSetters(problem.getJid(), ProblemSetterRole.EDITORIALIST, data.editorialistUsernames, setters, jidsMap);

        problemTagStore.updateTopicTags(problem.getJid(), Optional.ofNullable(data.tags).orElse(ImmutableMap.of()).keySet());

        return redirect(routes.ProblemController.viewProblem(problem.getId()));
    }

    @RequireCSRFCheck
    public Result switchLanguage(Http.Request req, long problemId) {
        String language = formFactory.form().bindFromRequest(req).get("langCode");

        return redirect(req.getHeaders().get("Referer").orElse(""))
                .addingToSession(req, newCurrentStatementLanguage(language));
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

    private Result showCreateProblem(Http.Request req, Form<ProblemCreateForm> form) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(createProblemView.render(form));
        template.setMainTitle("Create problem");
        template.markBreadcrumbLocation("Create problem", routes.ProblemController.createProblem());
        template.setPageTitle("Problem - Create");
        return renderTemplate(template);
    }

    private Result showEditProblem(Http.Request req, Form<ProblemEditForm> form, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editProblemView.render(form, problem));
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.markBreadcrumbLocation("Update problem", routes.ProblemController.editProblem(problem.getId()));
        template.setPageTitle("Problem - Update");
        return renderProblemTemplate(template, problem);
    }

    private Result renderProblemTemplate(HtmlTemplate template, Problem problem) {
        template.addSecondaryTab("View", routes.ProblemController.viewProblem(problem.getId()));
        if (problemRoleChecker.isAllowedToUpdateProblem(template.getRequest(), problem)) {
            template.addSecondaryTab("Update", routes.ProblemController.editProblem(problem.getId()));
        }

        return renderTemplate(template, problem);
    }
}
