package judgels.uriel.contest.problem;

import java.util.Map;
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

    public Map<String, String> findProblemAliasesByJids(String contestJid, Set<String> problemJids) {
        Map<String, String> problemAliases = problemDao.selectAllByContestJid(contestJid)
                .stream()
                .collect(Collectors.toMap(m -> m.problemJid, m -> m.alias));
        return problemJids
                .stream()
                .collect(Collectors.toMap(jid -> jid, problemAliases::get));
    }
}
