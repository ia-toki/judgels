package judgels.uriel.contest.problem;

import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.uriel.persistence.ContestProblemDao;

public class ContestProblemStore {
    private final ContestProblemDao problemDao;

    @Inject
    public ContestProblemStore(ContestProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public Set<String> getOpenProblemJids(String contestJid) {
        return problemDao.selectAllOpenByContestJid(contestJid)
                .stream()
                .map(model -> model.problemJid)
                .collect(Collectors.toSet());
    }
}
