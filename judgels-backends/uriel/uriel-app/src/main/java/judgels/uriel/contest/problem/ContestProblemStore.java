package judgels.uriel.contest.problem;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;

public class ContestProblemStore {
    private final ContestProblemDao problemDao;

    @Inject
    public ContestProblemStore(ContestProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public Set<ContestProblem> setProblems(String contestJid, List<ContestProblem> data) {
        Set<String> problemJids = data.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());
        for (ContestProblemModel model : problemDao.selectAllByContestJid(contestJid, createOptions())) {
            if (!problemJids.contains(model.problemJid)) {
                problemDao.delete(model);
            }
        }

        ImmutableSet.Builder<ContestProblem> problems = ImmutableSet.builder();
        for (ContestProblem problem : data) {
            problems.add(upsertProblem(
                    contestJid,
                    problem.getAlias(),
                    problem.getProblemJid(),
                    problem.getStatus(),
                    problem.getSubmissionsLimit(),
                    problem.getPoints()));
        }
        return problems.build();
    }

    private ContestProblem upsertProblem(
            String contestJid,
            String alias,
            String problemJid,
            ContestProblemStatus status,
            long submissionsLimit,
            Optional<Integer> points) {

        Optional<ContestProblemModel> maybeModel =
                problemDao.selectByContestJidAndProblemJid(contestJid, problemJid);
        if (maybeModel.isPresent()) {
            ContestProblemModel model = maybeModel.get();
            model.alias = alias;
            model.status = status.name();
            model.submissionsLimit = submissionsLimit;
            model.points = points.orElse(0);
            return fromModel(problemDao.update(model));
        } else {
            ContestProblemModel model = new ContestProblemModel();
            model.contestJid = contestJid;
            model.problemJid = problemJid;
            model.alias = alias;
            model.status = status.name();
            model.submissionsLimit = submissionsLimit;
            model.points = points.orElse(0);
            return fromModel(problemDao.insert(model));
        }
    }

    public Optional<ContestProblem> getProblem(String contestJid, String problemJid) {
        return problemDao.selectByContestJidAndProblemJid(contestJid, problemJid)
                .map(ContestProblemStore::fromModel);
    }

    public Optional<ContestProblem> getProblemByAlias(String contestJid, String problemAlias) {
        return problemDao.selectByContestJidAndProblemAlias(contestJid, problemAlias)
                .map(ContestProblemStore::fromModel);
    }

    public List<ContestProblem> getProblems(String contestJid) {
        return Lists.transform(
                problemDao.selectAllByContestJid(contestJid, createOptions()),
                ContestProblemStore::fromModel);
    }

    public List<String> getProblemJids(String contestJid) {
        return Lists.transform(
                problemDao.selectAllByContestJid(contestJid, createOptions()), model -> model.problemJid);
    }

    public List<String> getOpenProblemJids(String contestJid) {
        return Lists.transform(
                problemDao.selectAllOpenByContestJid(contestJid, createOptions()), model -> model.problemJid);
    }

    public boolean hasClosedProblems(String contestJid) {
        return problemDao.hasClosedByContestJid(contestJid);
    }

    public Map<String, String> getProblemAliasesByJids(String contestJid, Set<String> problemJids) {
        Map<String, String> problemAliases = problemDao.selectAllByContestJid(contestJid, createOptions())
                .stream()
                .collect(Collectors.toMap(m -> m.problemJid, m -> m.alias));
        return problemJids
                .stream()
                .collect(Collectors.toMap(jid -> jid, problemAliases::get));
    }

    public Map<String, Integer> getProblemPointsByJids(String contestJid, Set<String> problemJids) {
        return problemDao.selectAllByContestJid(contestJid, createOptions())
                .stream()
                .filter(m -> problemJids.contains(m.problemJid))
                .collect(Collectors.toMap(m -> m.problemJid, m -> m.points));
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static ContestProblem fromModel(ContestProblemModel model) {
        return new ContestProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .status(ContestProblemStatus.valueOf(model.status))
                .submissionsLimit(model.submissionsLimit)
                .points(model.points == 0 ? Optional.empty() : Optional.of(model.points))
                .build();
    }
}
