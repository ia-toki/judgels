package judgels.jerahmeel.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;

public class MoveProblemToProblemSetTask extends Task {
    private final ProblemDao problemDao;
    private final ProblemSetDao problemSetDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;

    public MoveProblemToProblemSetTask(
            ProblemDao problemDao,
            ProblemSetDao problemSetDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao) {

        super("jerahmeel-move-problem-to-problem-set");

        this.problemDao = problemDao;
        this.problemSetDao = problemSetDao;
        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter out) {
        List<String> problemSlugs = parameters.get("problemSlug");
        if (problemSlugs == null || problemSlugs.isEmpty()) {
            return;
        }
        String problemSlug = problemSlugs.get(0);

        List<String> toProblemSetJids = parameters.get("toProblemSetJid");
        if (toProblemSetJids == null || toProblemSetJids.isEmpty()) {
            return;
        }
        String toProblemSetJid = toProblemSetJids.get(0);

        List<String> aliases = parameters.get("alias");
        if (aliases == null || aliases.isEmpty()) {
            return;
        }
        String alias = aliases.get(0);

        Optional<ProblemModel> maybeProblemModel = problemDao.selectBySlug(problemSlug);
        if (maybeProblemModel.isEmpty()) {
            return;
        }
        String problemJid = maybeProblemModel.get().jid;

        Optional<ProblemSetModel> maybeProblemSetModel = problemSetDao.selectByJid(toProblemSetJid);
        if (maybeProblemSetModel.isEmpty()) {
            return;
        }

        List<ProblemSetProblemModel> problemSetProblemModels = problemSetProblemDao.selectAllByProblemJid(problemJid);
        if (!problemSetProblemModels.isEmpty()) {
            for (ProblemSetProblemModel model : problemSetProblemModels) {
                model.problemSetJid = toProblemSetJid;
                model.alias = alias;
                problemSetProblemDao.update(model);
            }
        } else {
            ProblemSetProblemModel model = new ProblemSetProblemModel();
            model.problemSetJid = toProblemSetJid;
            model.alias = alias;
            model.problemJid = problemJid;
            model.type = ProblemType.PROGRAMMING.name();
            problemSetProblemDao.insert(model);
        }

        Optional<ChapterProblemModel> maybeChapterProblemModel = chapterProblemDao.selectByProblemJid(problemJid);
        maybeChapterProblemModel.ifPresent(chapterProblemDao::delete);

        programmingSubmissionDao.updateContainerJid(problemJid, toProblemSetJid);
    }
}
