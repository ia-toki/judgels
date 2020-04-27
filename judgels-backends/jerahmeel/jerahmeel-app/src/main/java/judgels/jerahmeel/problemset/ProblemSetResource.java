package judgels.jerahmeel.problemset;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetProgress;
import judgels.jerahmeel.api.problemset.ProblemSetService;
import judgels.jerahmeel.api.problemset.ProblemSetStatsResponse;
import judgels.jerahmeel.api.problemset.ProblemSetUpdateData;
import judgels.jerahmeel.api.problemset.ProblemSetsResponse;
import judgels.jerahmeel.archive.ArchiveStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemSetResource implements ProblemSetService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ProblemSetStore problemSetStore;
    private final ArchiveStore archiveStore;
    private final StatsStore statsStore;

    @Inject
    public ProblemSetResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ProblemSetStore problemSetStore,
            ArchiveStore archiveStore,
            StatsStore statsStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.problemSetStore = problemSetStore;
        this.archiveStore = archiveStore;
        this.statsStore = statsStore;
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

        return new ProblemSetsResponse.Builder()
                .data(problemSets)
                .archiveSlugsMap(archiveSlugsMap)
                .archiveDescriptionsMap(archiveDescriptionsMap)
                .archiveName(archive.map(Archive::getName))
                .problemSetProgressesMap(problemSetProgressesMap)
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
}
