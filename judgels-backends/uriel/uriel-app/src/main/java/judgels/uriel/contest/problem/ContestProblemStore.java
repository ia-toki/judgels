package judgels.uriel.contest.problem;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestSubmissionDao;

public class ContestProblemStore {
    private final ContestProblemDao problemDao;
    private final ContestSubmissionDao submissionDao;

    @Inject
    public ContestProblemStore(ContestProblemDao problemDao, ContestSubmissionDao submissionDao) {
        this.problemDao = problemDao;
        this.submissionDao = submissionDao;
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

    public Optional<ContestContestantProblem> getContestantProblem(
            String contestJid,
            String userJid,
            String problemJid) {
        return problemDao.selectByContestJidAndProblemJid(contestJid, problemJid)
                .map(model -> contestantProblemFromModel(model, userJid));
    }

    public Optional<ContestContestantProblem> getContestantProblemByAlias(
            String contestJid,
            String userJid,
            String problemAlias) {
        return problemDao.selectByContestJidAndProblemAlias(contestJid, problemAlias)
                .map(model -> contestantProblemFromModel(model, userJid));
    }

    public List<ContestContestantProblem> getContestantProblems(String contestJid, String userJid) {
        List<ContestProblem> problems = getProblems(contestJid);
        Set<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, Long> submissionCounts = submissionDao.selectCounts(contestJid, userJid, problemJids);
        return Lists.transform(problems, problem ->
                new ContestContestantProblem.Builder()
                        .problem(problem)
                        .totalSubmissions(submissionCounts.getOrDefault(problem.getProblemJid(), 0L))
                        .build());
    }

    public List<String> getOpenProblemJids(String contestJid) {
        return Lists.transform(problemDao.selectAllOpenByContestJid(contestJid), model -> model.problemJid);
    }

    public List<ContestProblem> getProblems(String contestJid) {
        return problemDao.selectAllByContestJid(contestJid)
                .stream()
                .map(ContestProblemStore::fromModel)
                .collect(Collectors.toList());
    }

    public Map<String, String> getProblemAliasesByJids(String contestJid, Set<String> problemJids) {
        Map<String, String> problemAliases = problemDao.selectAllByContestJid(contestJid)
                .stream()
                .collect(Collectors.toMap(m -> m.problemJid, m -> m.alias));
        return problemJids
                .stream()
                .collect(Collectors.toMap(jid -> jid, problemAliases::get));
    }

    private static ContestProblem fromModel(ContestProblemModel model) {
        return new ContestProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .status(ContestProblemStatus.valueOf(model.status))
                .submissionsLimit(model.submissionsLimit)
                .build();
    }

    private ContestContestantProblem contestantProblemFromModel(ContestProblemModel model, String userJid) {
        long totalSubmissions = submissionDao.selectCounts(model.contestJid, userJid, ImmutableSet.of(model.problemJid))
                .getOrDefault(model.problemJid, 0L);
        return new ContestContestantProblem.Builder()
                .problem(fromModel(model))
                .totalSubmissions(totalSubmissions)
                .build();
    }
}
