package judgels.jerahmeel.problemset.problem;

import com.google.common.collect.ImmutableList;
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
import judgels.jerahmeel.persistence.ProblemContestDao;
import judgels.jerahmeel.persistence.ProblemContestModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.problem.ProblemType;

public class ProblemSetProblemStore {
    private final ProblemSetProblemDao problemDao;
    private final ProblemContestDao problemContestDao;

    @Inject
    public ProblemSetProblemStore(ProblemSetProblemDao problemDao, ProblemContestDao problemContestDao) {
        this.problemDao = problemDao;
        this.problemContestDao = problemContestDao;
    }

    public List<ProblemSetProblem> getProblems(String problemSetJid) {
        return Lists.transform(
                problemDao.selectAllByProblemSetJid(problemSetJid, createOptions()),
                m -> fromModel(m, getContestJids(m.problemJid)));
    }

    public Map<String, List<ProblemSetProblem>> getProblems(Set<String> problemSetJids) {
        return problemDao.selectAllByProblemSetJids(problemSetJids, createOptions())
                .stream()
                .collect(Collectors.groupingBy(
                        m -> m.problemSetJid,
                        Collectors.mapping(
                                m -> fromModel(m, ImmutableList.of()),
                                Collectors.toList())));
    }

    public Optional<ProblemSetProblem> getProblem(String problemJid) {
        return problemDao.selectByProblemJid(problemJid).map(m -> fromModel(m, getContestJids(m.problemJid)));
    }

    public Optional<ProblemSetProblem> getProblemByAlias(String problemSetJid, String problemAlias) {
        return problemDao.selectByProblemSetJidAndProblemAlias(problemSetJid, problemAlias)
                .map(m -> fromModel(m, getContestJids(m.problemJid)));
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
                    problem.getType(),
                    problem.getContestJids()));
        }
        return problems.build();
    }

    public ProblemSetProblem upsertProblem(
            String problemSetJid,
            String alias,
            String problemJid,
            ProblemType type,
            List<String> contestJids) {

        upsertProblemContests(problemJid, contestJids);

        Optional<ProblemSetProblemModel> maybeModel = problemDao.selectByProblemJid(problemJid);
        if (maybeModel.isPresent()) {
            ProblemSetProblemModel model = maybeModel.get();
            model.alias = alias;
            return fromModel(problemDao.update(model), contestJids);
        } else {
            ProblemSetProblemModel model = new ProblemSetProblemModel();
            model.problemSetJid = problemSetJid;
            model.alias = alias;
            model.problemJid = problemJid;
            model.type = type.name();
            return fromModel(problemDao.insert(model), contestJids);
        }
    }

    private void upsertProblemContests(String problemJid, List<String> contestJids) {
        problemContestDao.selectAllByProblemJid(problemJid).forEach(problemContestDao::delete);
        problemContestDao.flush();

        for (String contestJid : contestJids) {
            ProblemContestModel model = new ProblemContestModel();
            model.problemJid = problemJid;
            model.contestJid = contestJid;
            problemContestDao.insert(model);
        }
    }

    private List<String> getContestJids(String problemJid) {
        return Lists.transform(problemContestDao.selectAllByProblemJid(problemJid), m -> m.contestJid);
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static ProblemSetProblem fromModel(ProblemSetProblemModel model, List<String> contestJids) {
        return new ProblemSetProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .type(ProblemType.valueOf(model.type))
                .contestJids(contestJids)
                .build();
    }
}
