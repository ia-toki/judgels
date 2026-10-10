package judgels.catalog.problem;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemConfig;
import judgels.api.catalog.problem.ProblemCreateData;
import judgels.api.catalog.problem.ProblemErrors;
import judgels.api.catalog.problem.ProblemResponse;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.api.catalog.problem.ProblemUpdateData;
import judgels.api.catalog.problem.ProblemsResponse;
import judgels.catalog.WorldLanguageRegistry;
import judgels.catalog.problem.editorial.ProblemEditorialStore;
import judgels.catalog.problem.tag.ProblemTagStore;
import judgels.core.api.AuthHeader;
import judgels.grading.engines.GradingEngineRegistry;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.user.UserStore;

@Path("/api/v4/problems")
public class ProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemCreator problemCreator;
    @Inject protected ProblemUpdater problemUpdater;
    @Inject protected ProblemTagStore tagStore;
    @Inject protected ProblemEditorialStore editorialStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;

    @Inject public ProblemResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("term") @DefaultValue("") String termFilter,
            @QueryParam("tags") Set<String> tagsFilter,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        Optional<String> userJid = roleChecker.isAdmin(actorJid) ? Optional.empty() : Optional.of(actorJid);
        Page<Problem> problems = problemStore.getProblems(userJid, termFilter, tagsFilter, pageNumber, PAGE_SIZE);

        var authorJids = Lists.transform(problems.getPage(), Problem::getAuthorJid);

        return new ProblemsResponse.Builder()
                .data(problems)
                .profilesMap(profileStore.getProfiles(authorJids))
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Problem createProblem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ProblemCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        if (!isGradingEngineValid(data.getGradingEngine()) || !isLanguageValid(data.getInitialLanguage())) {
            throw new BadRequestException();
        }
        if (problemStore.problemExistsBySlug(data.getSlug())) {
            throw ProblemErrors.slugAlreadyExists(data.getSlug());
        }

        return problemCreator.createProblem(
                actorJid,
                data.getSlug(),
                data.getGradingEngine(),
                data.getAdditionalNote(),
                data.getInitialLanguage());
    }

    @GET
    @Path("/{problemJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemResponse getProblem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canView(actorJid, problem));

        Map<ProblemSetterRole, List<String>> setterJidsMap = problemStore.getProblemSetters(problemJid);

        Set<String> userJids = new HashSet<>();
        userJids.add(problem.getAuthorJid());
        setterJidsMap.values().forEach(userJids::addAll);

        return new ProblemResponse.Builder()
                .data(problem)
                .setterJidsMap(setterJidsMap)
                .topicTags(tagStore.findTopicTags(problemJid))
                .hasLocalChanges(problemStore.userCloneExists(actorJid, problemJid))
                .hasEditorial(editorialStore.hasEditorial(actorJid, problemJid))
                .config(new ProblemConfig.Builder()
                        .canEdit(roleChecker.canEdit(actorJid, problem))
                        .canManage(roleChecker.isAuthorOrAbove(actorJid, problem))
                        .build())
                .profilesMap(profileStore.getProfiles(userJids))
                .build();
    }

    @POST
    @Path("/{problemJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Problem updateProblem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            ProblemUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        if (!problem.getSlug().equals(data.getSlug()) && problemStore.problemExistsBySlug(data.getSlug())) {
            throw ProblemErrors.slugAlreadyExists(data.getSlug());
        }

        Set<String> usernames = new HashSet<>();
        data.getSetterUsernamesMap().values().forEach(usernames::addAll);
        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(usernames);

        Set<String> usernamesNotFound = Sets.difference(usernames, usernameToJidMap.keySet());
        if (!usernamesNotFound.isEmpty()) {
            throw ProblemErrors.setterUsernamesNotFound(new TreeSet<>(usernamesNotFound));
        }

        Map<ProblemSetterRole, List<String>> setterJidsMap = new HashMap<>();
        data.getSetterUsernamesMap().forEach((role, setterUsernames) -> setterJidsMap.put(
                role,
                setterUsernames.stream().map(usernameToJidMap::get).collect(Collectors.toList())));

        problemUpdater.updateProblem(
                problemJid,
                data.getSlug(),
                data.getAdditionalNote(),
                setterJidsMap,
                data.getTopicTags());

        return checkFound(problemStore.getProblemByJid(problemJid));
    }

    private static boolean isGradingEngineValid(String gradingEngine) {
        return gradingEngine.equals("Bundle")
                || GradingEngineRegistry.getInstance().getNamesMap().containsKey(gradingEngine);
    }

    private static boolean isLanguageValid(String language) {
        return WorldLanguageRegistry.getInstance().getLanguages().containsKey(language);
    }
}
