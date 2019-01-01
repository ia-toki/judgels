package judgels.uriel.contest.problem;

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
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;

public class ContestProblemStore {
    private final ContestProblemDao problemDao;

    @Inject
    public ContestProblemStore(ContestProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public void upsertProblem(String contestJid, ContestProblemData data) {
        Optional<ContestProblemModel> maybeModel =
                problemDao.selectByContestJidAndProblemJid(contestJid, data.getProblemJid());
        if (maybeModel.isPresent()) {
            ContestProblemModel model = maybeModel.get();
            model.alias = data.getAlias();
            model.status = data.getStatus().name();
            model.submissionsLimit = data.getSubmissionsLimit();
            problemDao.update(model);
        } else {
            ContestProblemModel model = new ContestProblemModel();
            model.contestJid = contestJid;
            model.problemJid = data.getProblemJid();
            model.alias = data.getAlias();
            model.status = data.getStatus().name();
            model.submissionsLimit = data.getSubmissionsLimit();
            problemDao.insert(model);
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
                .build();
    }
}
