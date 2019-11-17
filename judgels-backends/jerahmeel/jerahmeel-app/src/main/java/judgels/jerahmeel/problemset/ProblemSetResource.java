package judgels.jerahmeel.problemset;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetService;
import judgels.jerahmeel.api.problemset.ProblemSetsResponse;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemSetResource implements ProblemSetService {
    private final ActorChecker actorChecker;
    private final ProblemSetStore problemSetStore;

    @Inject
    public ProblemSetResource(ActorChecker actorChecker, ProblemSetStore problemSetStore) {
        this.actorChecker = actorChecker;
        this.problemSetStore = problemSetStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetsResponse getProblemSets(
            Optional<AuthHeader> authHeader,
            Optional<String> name,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        Page<ProblemSet> problemSets = problemSetStore.getProblemSets(name, page);
        return new ProblemSetsResponse.Builder()
                .data(problemSets)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSet getProblemSetBySlug(Optional<AuthHeader> authHeader, String problemSetSlug) {
        return checkFound(problemSetStore.getProblemSetBySlug(problemSetSlug));
    }
}
