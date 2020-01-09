package judgels.jerahmeel.problemset.problem;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public Optional<ProblemSetProblem> getProblem(String problemJid) {
        return problemDao.selectByProblemJid(problemJid).map(ProblemSetProblemStore::fromModel);
    }

    public Optional<ProblemSetProblem> getProblemByAlias(String problemSetJid, String problemAlias) {
        return problemDao.selectByProblemSetJidAndProblemAlias(problemSetJid, problemAlias)
                .map(ProblemSetProblemStore::fromModel);
    }

    public Map<String, String> getProblemAliasesByJids(Set<String> problemJids) {
        return problemDao.selectAllByProblemJids(problemJids)
                .stream()
                .filter(m -> problemJids.contains(m.problemJid))
                .collect(Collectors.toMap(m -> m.problemSetJid + "-" + m.problemJid, m -> m.alias));
    }

    public Set<ProblemSetProblem> setProblems(String problemSetJid, List<ProblemSetProblem> data) {
        Map<String, ProblemSetProblem> setProblems = data.stream().collect(
                Collectors.toMap(ProblemSetProblem::getProblemJid, Function.identity()));
        for (ProblemSetProblemModel model : problemDao.selectAllByProblemSetJid(problemSetJid, createOptions())) {
            ProblemSetProblem existingProblem = setProblems.get(model.problemJid);
            if (existingProblem == null || !existingProblem.getAlias().equals(model.alias)) {
                problemDao.delete(model);
            }
        }

        ImmutableSet.Builder<ProblemSetProblem> problems = ImmutableSet.builder();
        for (ProblemSetProblem problem : data) {
            problems.add(upsertProblem(
                    problemSetJid,
                    problem.getAlias(),
                    problem.getProblemJid(),
                    problem.getType()));
        }
        return problems.build();
    }

    public ProblemSetProblem upsertProblem(String problemSetJid, String alias, String problemJid, ProblemType type) {
        Optional<ProblemSetProblemModel> maybeModel = problemDao.selectByProblemJid(problemJid);
        if (maybeModel.isPresent()) {
            ProblemSetProblemModel model = maybeModel.get();
            model.alias = alias;
            return fromModel(problemDao.update(model));
        } else {
            ProblemSetProblemModel model = new ProblemSetProblemModel();
            model.problemSetJid = problemSetJid;
            model.alias = alias;
            model.problemJid = problemJid;
            model.type = type.name();
            model.status = "VISIBLE";
            return fromModel(problemDao.insert(model));
        }
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
