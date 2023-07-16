package judgels.jerahmeel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ChapterProblemDao extends Dao<ChapterProblemModel> {
    ChapterProblemQueryBuilder selectByChapterJid(String chapterJid);
    ChapterProblemQueryBuilder selectByChapterJids(Collection<String> chapterJids);

    Optional<ChapterProblemModel> selectByProblemJid(String problemJid);
    List<ChapterProblemModel> selectAllByProblemJids(Collection<String> problemJids);
    Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String problemAlias);

    interface ChapterProblemQueryBuilder extends QueryBuilder<ChapterProblemModel> {
        ChapterProblemQueryBuilder whereTypeIs(String type);
    }
}
