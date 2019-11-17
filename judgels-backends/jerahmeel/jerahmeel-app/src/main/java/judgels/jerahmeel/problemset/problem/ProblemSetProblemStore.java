package judgels.jerahmeel.problemset.problem;

import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class ProblemSetProblemStore {
    private final ProblemSetProblemDao problemDao;

    @Inject
    public ProblemSetProblemStore(ProblemSetProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public List<ProblemSetProblem> getProblems(String courseJid) {
        return Lists.transform(
                problemDao.selectAllByProblemSetJid(courseJid, createOptions()),
                ProblemSetProblemStore::fromModel);
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
                .build();
    }
}
