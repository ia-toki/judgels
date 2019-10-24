package judgels.jerahmeel.chapter.problem;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class ChapterProblemStore {
    private final ChapterProblemDao problemDao;

    @Inject
    public ChapterProblemStore(ChapterProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public List<ChapterProblem> getProblems(String chapterJid) {
        return Lists.transform(
                problemDao.selectAllByChapterJid(chapterJid, createOptions()),
                ChapterProblemStore::fromModel);
    }

    public Optional<ChapterProblem> getProblemByAlias(String chapterJid, String problemAlias) {
        return problemDao.selectByChapterJidAndProblemAlias(chapterJid, problemAlias)
                .map(ChapterProblemStore::fromModel);
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static ChapterProblem fromModel(ChapterProblemModel model) {
        return new ChapterProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .build();
    }
}
