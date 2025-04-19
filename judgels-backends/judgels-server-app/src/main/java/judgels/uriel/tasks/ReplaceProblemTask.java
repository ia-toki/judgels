package judgels.uriel.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;

public class ReplaceProblemTask extends Task {
    private final ProblemDao problemDao;
    private final ContestProblemDao contestProblemDao;
    private final ContestProgrammingSubmissionDao contestProgrammingSubmissionDao;
    private final ContestLogDao contestLogDao;
    private final ContestClarificationDao contestClarificationDao;

    public ReplaceProblemTask(
            ProblemDao problemDao,
            ContestProblemDao contestProblemDao,
            ContestProgrammingSubmissionDao contestProgrammingSubmissionDao,
            ContestClarificationDao contestClarificationDao,
            ContestLogDao contestLogDao) {

        super("uriel-replace-problem");

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
