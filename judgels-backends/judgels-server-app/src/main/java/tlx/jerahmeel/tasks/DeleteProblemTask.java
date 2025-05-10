package tlx.jerahmeel.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;

public class DeleteProblemTask extends Task {
    private final ProblemDao problemDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;
    private final ProgrammingGradingDao programmingGradingDao;
    private final BundleItemSubmissionDao bundleItemSubmissionDao;
    private final StatsUserProblemDao statsUserProblemDao;

    public DeleteProblemTask(
            ProblemDao problemDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao,
            ProgrammingGradingDao programmingGradingDao,
            BundleItemSubmissionDao bundleItemSubmissionDao,
            StatsUserProblemDao statsUserProblemDao) {

        super("jerahmeel-delete-problem");

        this.problemDao = problemDao;
        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
        this.bundleItemSubmissionDao = bundleItemSubmissionDao;
        this.statsUserProblemDao = statsUserProblemDao;
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter out) {
        List<String> problemSlugs = parameters.get("problemSlug");
        if (problemSlugs == null || problemSlugs.isEmpty()) {
            return;
        }
        String problemSlug = problemSlugs.get(0);

        Optional<ProblemModel> maybeProblemModel = problemDao.selectBySlug(problemSlug);
        if (maybeProblemModel.isEmpty()) {
            return;
        }
        String problemJid = maybeProblemModel.get().jid;

        if (problemJid.startsWith("JIDPROG")) {
            programmingGradingDao.deleteAllByProblemJid(problemJid);
            programmingSubmissionDao.deleteAllByProblemJid(problemJid);
        } else {
            bundleItemSubmissionDao.deleteAllByProblemJid(problemJid);
        }

        statsUserProblemDao.deleteAllByProblemJid(problemJid);
        chapterProblemDao.selectByProblemJid(problemJid).ifPresent(chapterProblemDao::delete);
        problemSetProblemDao.selectAllByProblemJid(problemJid).forEach(problemSetProblemDao::delete);
    }
}
