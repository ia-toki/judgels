package judgels.jerahmeel.problem;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;
import judgels.jerahmeel.api.problem.ProblemSetProblemInfo;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.api.Page;

public class ProblemStore {
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemDao;

    @Inject
    public ProblemStore(
            ProblemSetDao problemSetDao,
            ProblemSetProblemDao problemDao) {

        this.problemSetDao = problemSetDao;
        this.problemDao = problemDao;
    }

    public int getTotalProblems() {
        return problemDao.select().count();
    }

    public Page<ProblemSetProblemInfo> getProblems(Set<String> allowedProblemJids, int pageNumber, int pageSize) {
        Page<ProblemSetProblemModel> models = problemDao.selectPagedByDifficulty(allowedProblemJids, pageNumber, pageSize);

        var problemSetJids = Lists.transform(models.getPage(), m -> m.problemSetJid);
        Map<String, ProblemSetModel> problemSetsMap = problemSetDao.selectByJids(problemSetJids);

        return models.mapPage(p -> Lists.transform(p, m -> new ProblemSetProblemInfo.Builder()
                .problemSetSlug(problemSetsMap.get(m.problemSetJid).slug)
                .problemAlias(m.alias)
                .problemJid(m.problemJid)
                .build()));
    }
}
