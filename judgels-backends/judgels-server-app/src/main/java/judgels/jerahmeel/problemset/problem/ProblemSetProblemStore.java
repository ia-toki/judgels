package judgels.jerahmeel.problemset.problem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
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
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel_;
import judgels.persistence.api.OrderDir;
import judgels.sandalphon.api.problem.ProblemType;

public class ProblemSetProblemStore {
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemDao;
    private final ProblemContestDao problemContestDao;

    @Inject
    public ProblemSetProblemStore(
            ProblemSetDao problemSetDao,
            ProblemSetProblemDao problemDao,
            ProblemContestDao problemContestDao) {

        this.problemSetDao = problemSetDao;
        this.problemDao = problemDao;
        this.problemContestDao = problemContestDao;
    }

    public List<ProblemSetProblem> getProblems(String problemSetJid) {
        return Lists.transform(
                problemDao.selectByProblemSetJid(problemSetJid).orderBy(ProblemSetProblemModel_.ALIAS, OrderDir.ASC).all(),
                m -> fromModel(m, getContestJids(m.problemJid)));
    }

    public Map<String, List<ProblemSetProblem>> getProblems(Collection<String> problemSetJids) {
        return problemDao.selectByProblemSetJids(problemSetJids).orderBy(ProblemSetProblemModel_.ALIAS, OrderDir.ASC).all()
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

    public Map<String, String> getProblemAliasesByJids(Collection<String> problemJids) {
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

        List<ProblemSetProblemModel> existingProblems = problemDao.selectByProblemSetJid(problemSetJid).all();
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

    public Map<String, List<List<String>>> getProblemSetProblemPathsMap(Collection<String> problemJids) {
        List<ProblemSetProblemModel> models = problemDao.selectAllByProblemJids(problemJids);

        List<String> problemSetJids = Lists.transform(models, m -> m.problemSetJid);
        Map<String, ProblemSetModel> problemSetModelsMap = problemSetDao.selectByJids(problemSetJids);

        Map<String, List<List<String>>> problemSetProblemPathsMap = new HashMap<>();
        for (ProblemSetProblemModel m : models) {
            if (!problemSetModelsMap.containsKey(m.problemSetJid)) {
                continue;
            }
            ProblemSetModel problemSetModel = problemSetModelsMap.get(m.problemSetJid);
            List<String> path = List.of(problemSetModel.slug, m.alias);
            problemSetProblemPathsMap.putIfAbsent(m.problemJid, new ArrayList<>());
            problemSetProblemPathsMap.get(m.problemJid).add(path);
        }
        return Map.copyOf(problemSetProblemPathsMap);
    }

    public List<List<String>> getProblemSetProblemPaths(String problemJid) {
        return getProblemSetProblemPathsMap(List.of(problemJid)).getOrDefault(problemJid, List.of());
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

    private static ProblemSetProblem fromModel(ProblemSetProblemModel model, List<String> contestJids) {
        return new ProblemSetProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .type(ProblemType.valueOf(model.type))
                .contestJids(contestJids)
                .build();
    }
}
