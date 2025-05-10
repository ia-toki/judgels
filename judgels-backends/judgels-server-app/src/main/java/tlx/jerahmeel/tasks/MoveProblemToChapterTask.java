package tlx.jerahmeel.tasks;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;

public class MoveProblemToChapterTask extends Task {
    private final ProblemDao problemDao;
    private final ChapterDao chapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;

    public MoveProblemToChapterTask(
            ProblemDao problemDao,
            ChapterDao chapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao) {

        super("jerahmeel-move-problem-to-chapter");

        this.problemDao = problemDao;
        this.chapterDao = chapterDao;
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

        List<String> toChapterJids = parameters.get("toChapterJid");
        if (toChapterJids == null || toChapterJids.isEmpty()) {
            return;
        }
        String toChapterJid = toChapterJids.get(0);

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

        Optional<ChapterModel> maybeChapterModel = chapterDao.selectByJid(toChapterJid);
        if (maybeChapterModel.isEmpty()) {
            return;
        }

        Optional<ChapterProblemModel> maybeChapterProblemModel = chapterProblemDao.selectByProblemJid(problemJid);
        if (maybeChapterProblemModel.isPresent()) {
            ChapterProblemModel model = maybeChapterProblemModel.get();

            model.chapterJid = toChapterJid;
            model.alias = alias;
            chapterProblemDao.update(model);
        } else {
            ChapterProblemModel model = new ChapterProblemModel();
            model.chapterJid = toChapterJid;
            model.alias = alias;
            model.problemJid = problemJid;
            model.type = ProblemType.PROGRAMMING.name();
            chapterProblemDao.insert(model);
        }

        List<ProblemSetProblemModel> problemSetProblemModels = problemSetProblemDao.selectAllByProblemJid(problemJid);
        problemSetProblemModels.forEach(problemSetProblemDao::delete);

        programmingSubmissionDao.updateContainerJid(problemJid, toChapterJid);
    }
}
