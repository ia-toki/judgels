package judgels.jerahmeel.problemset;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetProgress;
import judgels.jerahmeel.api.problemset.ProblemSetService;
import judgels.jerahmeel.api.problemset.ProblemSetStatsResponse;
import judgels.jerahmeel.api.problemset.ProblemSetUpdateData;
import judgels.jerahmeel.api.problemset.ProblemSetUserProgressesData;
import judgels.jerahmeel.api.problemset.ProblemSetUserProgressesResponse;
import judgels.jerahmeel.api.problemset.ProblemSetsResponse;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.archive.ArchiveStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemSetResource implements ProblemSetService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ProblemSetStore problemSetStore;
    private final ProblemSetProblemStore problemSetProblemStore;
    private final ArchiveStore archiveStore;
    private final StatsStore statsStore;
    private final UserClient userClient;

    @Inject
    public ProblemSetResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ProblemSetStore problemSetStore,
            ProblemSetProblemStore problemSetProblemStore,
            ArchiveStore archiveStore,
            StatsStore statsStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.problemSetStore = problemSetStore;
        this.problemSetProblemStore = problemSetProblemStore;
        this.archiveStore = archiveStore;
        this.statsStore = statsStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetsResponse getProblemSets(
            Optional<AuthHeader> authHeader,
            Optional<String> archiveSlug,
            Optional<String> name,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        Optional<Archive> archive = archiveSlug.flatMap(archiveStore::getArchiveBySlug);
        Optional<String> archiveJid = archiveSlug.isPresent()
                ? Optional.of(archive.map(Archive::getJid).orElse(""))
                : Optional.empty();
        Page<ProblemSet> problemSets = problemSetStore.getProblemSets(archiveJid, name, page);
        Set<String> problemSetJids = problemSets.getPage().stream().map(ProblemSet::getJid).collect(toSet());
        Set<String> archiveJids = problemSets.getPage().stream().map(ProblemSet::getArchiveJid).collect(toSet());
        Map<String, Archive> archivesMap = archiveStore.getArchivesByJids(archiveJids);
        Map<String, String> archiveSlugsMap = archivesMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getSlug()));
        Map<String, String> archiveDescriptionsMap = archivesMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDescription()));
        Map<String, ProblemSetProgress> problemSetProgressesMap =
                statsStore.getProblemSetProgressesMap(actorJid, problemSetJids);

        String descriptions = "";
        for (String description : archiveDescriptionsMap.values()) {
            descriptions = descriptions.concat(description);
        }
        for (ProblemSet problemSet : problemSets.getPage()) {
            descriptions = descriptions.concat(problemSet.getDescription());
        }
        Map<String, Profile> profilesMap = userClient.parseProfiles(descriptions);

        return new ProblemSetsResponse.Builder()
                .data(problemSets)
                .archiveSlugsMap(archiveSlugsMap)
                .archiveDescriptionsMap(archiveDescriptionsMap)
                .archiveName(archive.map(Archive::getName))
                .problemSetProgressesMap(problemSetProgressesMap)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetStatsResponse getProblemSetStats(Optional<AuthHeader> authHeader, String problemSetJid) {
        String actorJid = actorChecker.check(authHeader);
        ProblemSetProgress progress = statsStore
                .getProblemSetProgressesMap(actorJid, ImmutableSet.of(problemSetJid))
                .get(problemSetJid);
        return new ProblemSetStatsResponse.Builder()
                .progress(progress)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSet getProblemSetBySlug(Optional<AuthHeader> authHeader, String problemSetSlug) {
        return checkFound(problemSetStore.getProblemSetBySlug(problemSetSlug));
    }

    @Override
    @UnitOfWork
    public ProblemSet createProblemSet(AuthHeader authHeader, ProblemSetCreateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return problemSetStore.createProblemSet(data);
    }

    @Override
    @UnitOfWork
    public ProblemSet updateProblemSet(AuthHeader authHeader, String problemSetJid, ProblemSetUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return checkFound(problemSetStore.updateProblemSet(problemSetJid, data));
    }

    @Override
    @UnitOfWork
    public ProblemSet searchProblemSet(String contestJid) {
        return checkFound(problemSetStore.getProblemSetByContestJid(contestJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetUserProgressesResponse getProblemSetUserProgresses(ProblemSetUserProgressesData data) {
        checkArgument(data.getUsernames().size() <= 100, "Cannot get more than 100 users.");
        checkArgument(data.getProblemSetSlugs().size() <= 20, "Cannot get more than 100 problemsets.");

        Set<String> problemSetSlugs = ImmutableSet.copyOf(data.getProblemSetSlugs());
        Map<String, ProblemSet> problemSetsMap = problemSetStore.getProblemSetsBySlugs(problemSetSlugs);

        Set<String> problemSetJids = problemSetsMap
                .values()
                .stream()
                .map(ProblemSet::getJid)
                .collect(Collectors.toSet());
        Map<String, List<ProblemSetProblem>> problemsMap = problemSetProblemStore.getProblems(problemSetJids);

        Set<String> usernames = ImmutableSet.copyOf(data.getUsernames());
        Map<String, String> usernameToJidsMap = userClient.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidsMap.values());
        Set<String> problemJids = problemsMap
                .values()
                .stream()
                .flatMap(problems -> Lists.transform(problems, ProblemSetProblem::getProblemJid).stream())
                .collect(Collectors.toSet());
        Map<String, Map<String, ProblemProgress>> progressesMap =
                statsStore.getUserProblemProgressesMap(userJids, problemJids);

        Map<String, Map<String, Map<String, ProblemProgress>>> userProgressesMap = new LinkedHashMap<>();
        for (String username : data.getUsernames()) {
            if (!usernameToJidsMap.containsKey(username)) {
                continue;
            }

            String userJid = usernameToJidsMap.get(username);
            userProgressesMap.put(username, new LinkedHashMap<>());

            for (String problemSetSlug : data.getProblemSetSlugs()) {
                if (!problemSetsMap.containsKey(problemSetSlug)) {
                    continue;
                }

                String problemSetJid = problemSetsMap.get(problemSetSlug).getJid();
                userProgressesMap.get(username).put(problemSetSlug, new LinkedHashMap<>());

                if (!progressesMap.containsKey(userJid)) {
                    continue;
                }

                for (ProblemSetProblem problem : problemsMap.get(problemSetJid)) {
                    if (progressesMap.get(userJid).containsKey(problem.getProblemJid())) {
                        userProgressesMap.get(username).get(problemSetSlug).put(
                                problem.getAlias(),
                                progressesMap.get(userJid).get(problem.getProblemJid()));
                    }
                }
            }
        }

        return new ProblemSetUserProgressesResponse.Builder()
                .userProgressesMap(userProgressesMap)
                .build();
    }
}
