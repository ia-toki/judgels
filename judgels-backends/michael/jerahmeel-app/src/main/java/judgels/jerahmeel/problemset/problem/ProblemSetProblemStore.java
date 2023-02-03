package judgels.jerahmeel.problemset.problem;

import static judgels.persistence.api.SelectionOptions.DEFAULT_ALL;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    public Optional<ProblemSetProblem> getProblem(String problemSetJid, String problemJid) {
        return problemDao.selectByProblemSetJidAndProblemJid(problemSetJid, problemJid)
                .map(m -> fromModel(m, getContestJids(m.problemJid)));
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

    public Map<String, Boolean> setProblems(String problemSetJid, List<ProblemSetProblem> data) {
        Set<String> affectedProblemJids = new HashSet<>();

        Map<String, ProblemSetProblem> problemsToSet = new HashMap<>();
        for (ProblemSetProblem p : data) {
            problemsToSet.put(p.getProblemJid(), p);
            affectedProblemJids.add(p.getProblemJid());
        }

        List<ProblemSetProblemModel> existingProblems = problemDao.selectAllByProblemSetJid(problemSetJid, DEFAULT_ALL);
        for (ProblemSetProblemModel model : existingProblems) {
            ProblemSetProblem problemToSet = problemsToSet.get(model.problemJid);
            if (problemToSet == null || !problemToSet.getAlias().equals(model.alias)) {
                problemDao.delete(model);
            }
            affectedProblemJids.add(model.problemJid);
        }

        for (ProblemSetProblem problem : data) {
            upsertProblem(
                    problemSetJid,
                    problem.getAlias(),
                    problem.getProblemJid(),
                    problem.getType(),
                    problem.getContestJids());
        }

        Set<String> visibleProblemJids = problemDao.selectAllByProblemJids(affectedProblemJids)
                .stream()
                .map(m -> m.problemJid)
                .collect(Collectors.toSet());

        ImmutableMap.Builder<String, Boolean> problemVisibilitiesMap = ImmutableMap.builder();
        for (String problemJid : affectedProblemJids) {
            problemVisibilitiesMap.put(problemJid, visibleProblemJids.contains(problemJid));
        }
        return problemVisibilitiesMap.build();
    }

    public ProblemSetProblem upsertProblem(
            String problemSetJid,
            String alias,
            String problemJid,
            ProblemType type,
            List<String> contestJids) {

        upsertProblemContests(problemJid, contestJids);

        Optional<ProblemSetProblemModel> maybeModel =
                problemDao.selectByProblemSetJidAndProblemJid(problemSetJid, problemJid);
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
        return new SelectionOptions.Builder().from(DEFAULT_ALL)
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
