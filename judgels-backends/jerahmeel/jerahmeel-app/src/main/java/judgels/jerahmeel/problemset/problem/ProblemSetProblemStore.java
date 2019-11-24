package judgels.jerahmeel.problemset.problem;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.problem.ProblemType;

public class ProblemSetProblemStore {
    private final ProblemSetProblemDao problemDao;

    @Inject
    public ProblemSetProblemStore(ProblemSetProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public List<ProblemSetProblem> getProblems(String problemSetJid) {
        return Lists.transform(
                problemDao.selectAllByProblemSetJid(problemSetJid, createOptions()),
                ProblemSetProblemStore::fromModel);
    }

    public Optional<ProblemSetProblem> getProblemByAlias(String problemSetJid, String problemAlias) {
        return problemDao.selectByProblemSetJidAndProblemAlias(problemSetJid, problemAlias)
                .map(ProblemSetProblemStore::fromModel);
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static ProblemSetProblem fromModel(ProblemSetProblemModel model) {
        return new ProblemSetProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .type(ProblemType.valueOf(model.type))
                .build();
    }
}
