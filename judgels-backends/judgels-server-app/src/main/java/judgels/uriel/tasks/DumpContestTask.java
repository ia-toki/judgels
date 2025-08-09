package judgels.uriel.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestSupervisorDao;

public class DumpContestTask extends Task {
    private final ContestDao contestDao;
    private final ContestModuleDao moduleDao;
    private final ContestManagerDao managerDao;
    private final ContestSupervisorDao supervisorDao;
    private final ContestContestantDao contestantDao;
    private final ContestProblemDao problemDao;
    private final ContestAnnouncementDao announcementDao;
    private final ContestClarificationDao clarificationDao;
    private final ContestProgrammingSubmissionDao programmingSubmissionDao;
    private final ContestProgrammingGradingDao programmingGradingDao;
    private final ContestScoreboardDao scoreboardDao;
    private final ContestLogDao logDao;

    public DumpContestTask(
            ContestDao contestDao,
            ContestModuleDao moduleDao,
            ContestManagerDao managerDao,
            ContestSupervisorDao supervisorDao,
            ContestContestantDao contestantDao,
            ContestProblemDao problemDao,
            ContestAnnouncementDao announcementDao,
            ContestClarificationDao clarificationDao,
            ContestScoreboardDao scoreboardDao,
            ContestLogDao logDao,
            ContestProgrammingSubmissionDao programmingSubmissionDao,
            ContestProgrammingGradingDao programmingGradingDao) {

        super("uriel-dump-contest");

        this.contestDao = contestDao;
        this.moduleDao = moduleDao;
        this.managerDao = managerDao;
        this.supervisorDao = supervisorDao;
        this.contestantDao = contestantDao;
        this.problemDao = problemDao;
        this.announcementDao = announcementDao;
        this.clarificationDao = clarificationDao;
        this.scoreboardDao = scoreboardDao;
        this.logDao = logDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public void execute(Map<String, List<String>> parameters, PrintWriter output) {
        List<String> contestSlugs = parameters.get("contestSlug");
        if (contestSlugs == null || contestSlugs.isEmpty()) {
            return;
        }
        String contestSlug = contestSlugs.get(0);
        Optional<ContestModel> maybeModel = contestDao.selectBySlug(contestSlug);
        if (maybeModel.isEmpty()) {
            return;
        }

        String contestJid = maybeModel.get().jid;

        contestDao.dump(output, contestJid);
        moduleDao.dump(output, contestJid);
        managerDao.dump(output, contestJid);
        supervisorDao.dump(output, contestJid);
        contestantDao.dump(output, contestJid);
        problemDao.dump(output, contestJid);
        announcementDao.dump(output, contestJid);
        clarificationDao.dump(output, contestJid);
        scoreboardDao.dump(output, contestJid);
        logDao.dump(output, contestJid);

        Collection<String> submissionJids = programmingSubmissionDao.dump(output, contestJid);
        programmingGradingDao.dump(output, submissionJids);
    }
}
