package judgels.uriel.contest.problem;

import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemDao.ContestProblemQueryBuilder;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProblemModel_;

public class ContestProblemStore {
    private final ContestProblemDao problemDao;

    @Inject
    public ContestProblemStore(ContestProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public Set<ContestProblem> setProblems(String contestJid, List<ContestProblem> data) {
        Map<String, ContestProblem> setProblems = data.stream().collect(
                Collectors.toMap(ContestProblem::getProblemJid, Function.identity()));
        for (ContestProblemModel model : selectProblemsByContestJid(contestJid).all()) {
            ContestProblem existingProblem = setProblems.get(model.problemJid);
            if (existingProblem == null
                    || !existingProblem.getAlias().equals(model.alias)
                    || !existingProblem.getPoints().orElse(0).equals(model.points)) {

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
            Optional<Long> submissionsLimit,
            Optional<Integer> points) {

        Optional<ContestProblemModel> maybeModel =
                problemDao.selectByContestJidAndProblemJid(contestJid, problemJid);
        if (maybeModel.isPresent()) {
            ContestProblemModel model = maybeModel.get();
            model.alias = alias;
            model.status = status.name();
            model.submissionsLimit = submissionsLimit.orElse(0L);
            model.points = points.orElse(0);
            return fromModel(problemDao.update(model));
        } else {
            ContestProblemModel model = new ContestProblemModel();
            model.contestJid = contestJid;
            model.problemJid = problemJid;
            model.alias = alias;
            model.status = status.name();
            model.submissionsLimit = submissionsLimit.orElse(0L);
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
                selectProblemsByContestJid(contestJid).all(),
                ContestProblemStore::fromModel);
    }

    public List<String> getProblemJids(String contestJid) {
        return Lists.transform(
                selectProblemsByContestJid(contestJid).all(),
                model -> model.problemJid);
    }

    public List<String> getOpenProblemJids(String contestJid) {
        return Lists.transform(
                selectProblemsByContestJid(contestJid).whereStatusIs(OPEN.name()).all(),
                model -> model.problemJid);
    }

    public boolean hasClosedProblems(String contestJid) {
        return problemDao
                .selectByContestJid(contestJid)
                .whereStatusIs(CLOSED.name())
                .count() > 0;
    }

    public Map<String, String> getProblemAliasesByJids(String contestJid, Collection<String> problemJids) {
        Map<String, String> problemAliases = problemDao.selectByContestJid(contestJid).all()
                .stream()
                .collect(Collectors.toMap(m -> m.problemJid, m -> m.alias));
        return problemJids
                .stream()
                .filter(problemAliases::containsKey)
                .collect(Collectors.toMap(jid -> jid, problemAliases::get));
    }

    private ContestProblemQueryBuilder selectProblemsByContestJid(String contestJid) {
        return problemDao
                .selectByContestJid(contestJid)
                .orderBy(ContestProblemModel_.ALIAS, OrderDir.ASC);
    }

    private static ContestProblem fromModel(ContestProblemModel model) {
        return new ContestProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .status(ContestProblemStatus.valueOf(model.status))
                .submissionsLimit(model.submissionsLimit == 0 ? Optional.empty() : Optional.of(model.submissionsLimit))
                .points(model.points == 0 ? Optional.empty() : Optional.of(model.points))
                .build();
    }
}
