package tlx.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.persistence.dao.ContestClarificationDao;
import judgels.persistence.dao.ContestLogDao;
import judgels.persistence.dao.ContestProblemDao;
import judgels.persistence.dao.ContestProgrammingSubmissionDao;
import judgels.persistence.dao.ProblemDao;
import judgels.persistence.model.ProblemModel;

public class ReplaceContestProblemTask extends Task {
    private final ProblemDao problemDao;
    private final ContestProblemDao contestProblemDao;
    private final ContestProgrammingSubmissionDao contestProgrammingSubmissionDao;
    private final ContestLogDao contestLogDao;
    private final ContestClarificationDao contestClarificationDao;

    public ReplaceContestProblemTask(
            ProblemDao problemDao,
            ContestProblemDao contestProblemDao,
            ContestProgrammingSubmissionDao contestProgrammingSubmissionDao,
            ContestClarificationDao contestClarificationDao,
            ContestLogDao contestLogDao) {

        super("replace-contest-problem");

        this.problemDao = problemDao;
        this.contestProblemDao = contestProblemDao;
        this.contestProgrammingSubmissionDao = contestProgrammingSubmissionDao;
        this.contestClarificationDao = contestClarificationDao;
        this.contestLogDao = contestLogDao;
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
        List<String> problemSlugs = parameters.get("oldProblemSlug");
        if (problemSlugs == null || problemSlugs.isEmpty()) {
            return;
        }
        String oldProblemSlug = problemSlugs.get(0);

        Optional<ProblemModel> maybeProblemModel = problemDao.selectBySlug(oldProblemSlug);
        if (maybeProblemModel.isEmpty()) {
            return;
        }
        String oldProblemJid = maybeProblemModel.get().jid;

        problemSlugs = parameters.get("newProblemSlug");
        if (problemSlugs == null || problemSlugs.isEmpty()) {
            return;
        }
        String newProblemSlug = problemSlugs.get(0);

        maybeProblemModel = problemDao.selectBySlug(newProblemSlug);
        if (maybeProblemModel.isEmpty()) {
            return;
        }
        String newProblemJid = maybeProblemModel.get().jid;

        contestProblemDao.updateProblemJid(oldProblemJid, newProblemJid);
        contestProgrammingSubmissionDao.updateProblemJid(oldProblemJid, newProblemJid);
        contestClarificationDao.updateTopicJid(oldProblemJid, newProblemJid);
        contestLogDao.updateProblemJid(oldProblemJid, newProblemJid);
    }
}
