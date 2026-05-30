package tlx.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.persistence.dao.BundleItemSubmissionDao;
import judgels.persistence.dao.ChapterProblemDao;
import judgels.persistence.dao.ProblemDao;
import judgels.persistence.dao.ProblemSetProblemDao;
import judgels.persistence.dao.StatsUserProblemDao;
import judgels.persistence.dao.TrainingProgrammingGradingDao;
import judgels.persistence.dao.TrainingProgrammingSubmissionDao;
import judgels.persistence.model.ProblemModel;

public class DeleteTrainingProblemTask extends Task {
    private final ProblemDao problemDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final TrainingProgrammingSubmissionDao programmingSubmissionDao;
    private final TrainingProgrammingGradingDao programmingGradingDao;
    private final BundleItemSubmissionDao bundleItemSubmissionDao;
    private final StatsUserProblemDao statsUserProblemDao;

    public DeleteTrainingProblemTask(
            ProblemDao problemDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao,
            TrainingProgrammingGradingDao programmingGradingDao,
            BundleItemSubmissionDao bundleItemSubmissionDao,
            StatsUserProblemDao statsUserProblemDao) {

        super("delete-training-problem");

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
