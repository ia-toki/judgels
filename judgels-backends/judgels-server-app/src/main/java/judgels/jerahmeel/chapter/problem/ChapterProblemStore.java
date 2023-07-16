package judgels.jerahmeel.chapter.problem;

import static java.util.stream.Collectors.toMap;
import static judgels.sandalphon.api.problem.ProblemType.BUNDLE;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ChapterProblemModel_;
import judgels.persistence.api.OrderDir;
import judgels.sandalphon.api.problem.ProblemType;

public class ChapterProblemStore {
    private final ChapterProblemDao problemDao;

    @Inject
    public ChapterProblemStore(ChapterProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    public List<ChapterProblem> getProblems(String chapterJid) {
        return Lists.transform(problemDao
                .selectByChapterJid(chapterJid)
                .orderBy(ChapterProblemModel_.ALIAS, OrderDir.ASC)
                .all(), ChapterProblemStore::fromModel);
    }

    public Optional<ChapterProblem> getProblem(String problemJid) {
        return problemDao.selectByProblemJid(problemJid).map(ChapterProblemStore::fromModel);
    }

    public List<String> getBundleProblemJids(String chapterJid) {
        return Lists.transform(problemDao
                .selectByChapterJid(chapterJid)
                .whereTypeIs(BUNDLE.name())
                .orderBy(ChapterProblemModel_.ALIAS, OrderDir.ASC)
                .all(), model -> model.problemJid);
    }

    public List<String> getProgrammingProblemJids(String chapterJid) {
        return Lists.transform(problemDao
                .selectByChapterJid(chapterJid)
                .whereTypeIs(PROGRAMMING.name())
                .all(), model -> model.problemJid);
    }

    public Optional<ChapterProblem> getProblemByAlias(String chapterJid, String problemAlias) {
        return problemDao.selectByChapterJidAndProblemAlias(chapterJid, problemAlias)
                .map(ChapterProblemStore::fromModel);
    }

    public Map<String, String> getProblemAliasesByJids(Collection<String> problemJids) {
        return problemDao.selectAllByProblemJids(problemJids)
                .stream()
                .filter(m -> problemJids.contains(m.problemJid))
                .collect(toMap(m -> m.chapterJid + "-" + m.problemJid, m -> m.alias));
    }

    public Set<ChapterProblem> setProblems(String chapterJid, List<ChapterProblem> data) {
        Map<String, ChapterProblem> setProblems = data.stream().collect(
                toMap(ChapterProblem::getProblemJid, Function.identity()));
        for (ChapterProblemModel model : problemDao.selectByChapterJid(chapterJid).all()) {
            ChapterProblem existingProblem = setProblems.get(model.problemJid);
            if (existingProblem == null || !existingProblem.getAlias().equals(model.alias)) {
                problemDao.delete(model);
            }
        }

        ImmutableSet.Builder<ChapterProblem> problems = ImmutableSet.builder();
        for (ChapterProblem problem : data) {
            problems.add(upsertProblem(
                    chapterJid,
                    problem.getAlias(),
                    problem.getProblemJid(),
                    problem.getType()));
        }
        return problems.build();
    }

    public ChapterProblem upsertProblem(String chapterJid, String alias, String problemJid, ProblemType type) {
        Optional<ChapterProblemModel> maybeModel = problemDao.selectByProblemJid(problemJid);
        if (maybeModel.isPresent()) {
            ChapterProblemModel model = maybeModel.get();
            model.alias = alias;
            return fromModel(problemDao.update(model));
        } else {
            ChapterProblemModel model = new ChapterProblemModel();
            model.chapterJid = chapterJid;
            model.alias = alias;
            model.problemJid = problemJid;
            model.type = type.name();
            return fromModel(problemDao.insert(model));
        }
    }

    private static ChapterProblem fromModel(ChapterProblemModel model) {
        return new ChapterProblem.Builder()
                .problemJid(model.problemJid)
                .alias(model.alias)
                .type(ProblemType.valueOf(model.type))
                .build();
    }
}
