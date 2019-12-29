package judgels.jerahmeel.problemset;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetService;
import judgels.jerahmeel.api.problemset.ProblemSetsResponse;
import judgels.jerahmeel.archive.ArchiveStore;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemSetResource implements ProblemSetService {
    private final ActorChecker actorChecker;
    private final ProblemSetStore problemSetStore;
    private final ArchiveStore archiveStore;

    @Inject
    public ProblemSetResource(ActorChecker actorChecker, ProblemSetStore problemSetStore, ArchiveStore archiveStore) {
        this.actorChecker = actorChecker;
        this.problemSetStore = problemSetStore;
        this.archiveStore = archiveStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetsResponse getProblemSets(
            Optional<AuthHeader> authHeader,
            Optional<String> archiveSlug,
            Optional<String> name,
            Optional<Integer> page) {

        actorChecker.check(authHeader);

        Optional<Archive> archive = archiveSlug.flatMap(archiveStore::getArchiveBySlug);
        Optional<String> archiveJid = archiveSlug.isPresent()
                ? Optional.of(archive.map(Archive::getJid).orElse(""))
                : Optional.empty();
        Page<ProblemSet> problemSets = problemSetStore.getProblemSets(archiveJid, name, page);
        Set<String> archiveJids = problemSets.getPage().stream().map(ProblemSet::getArchiveJid).collect(toSet());
        Map<String, String> archiveDescriptionsMap = archiveStore.getArchivesByJids(archiveJids).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDescription()));
        return new ProblemSetsResponse.Builder()
                .data(problemSets)
                .archiveDescriptionsMap(archiveDescriptionsMap)
                .archiveName(archive.map(Archive::getName))
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSet getProblemSetBySlug(Optional<AuthHeader> authHeader, String problemSetSlug) {
        return checkFound(problemSetStore.getProblemSetBySlug(problemSetSlug));
    }
}
