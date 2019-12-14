package judgels.jerahmeel.chapter.problem;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.problem.ProblemType;

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

    public Optional<ChapterProblem> getProblem(String problemJid) {
        return problemDao.selectByProblemJid(problemJid).map(ChapterProblemStore::fromModel);
    }

    public List<String> getBundleProblemJids(String contestJid) {
        return Lists.transform(
                problemDao.selectAllBundleByChapterJid(contestJid, createOptions()), model -> model.problemJid);
    }

    public List<String> getProgrammingProblemJids(String contestJid) {
        return Lists.transform(
                problemDao.selectAllProgrammingByChapterJid(contestJid, createOptions()), model -> model.problemJid);
    }

    public Optional<ChapterProblem> getProblemByAlias(String chapterJid, String problemAlias) {
        return problemDao.selectByChapterJidAndProblemAlias(chapterJid, problemAlias)
                .map(ChapterProblemStore::fromModel);
    }

    public Map<String, String> getProblemAliasesByJids(Set<String> problemJids) {
        Map<String, String> problemAliases = problemDao.selectAllByProblemJids(problemJids)
                .stream()
                .collect(Collectors.toMap(m -> m.problemJid, m -> m.alias));
        return problemJids
                .stream()
                .filter(problemAliases::containsKey)
                .collect(Collectors.toMap(jid -> jid, problemAliases::get));
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
                .type(ProblemType.valueOf(model.type))
                .build();
    }
}
