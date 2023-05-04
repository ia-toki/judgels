package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ChapterProblemDao extends Dao<ChapterProblemModel> {
    ChapterProblemQueryBuilder selectByChapterJid(String chapterJid);
    ChapterProblemQueryBuilder selectByChapterJids(Set<String> chapterJids);

    Optional<ChapterProblemModel> selectByProblemJid(String problemJid);
    List<ChapterProblemModel> selectAllByProblemJids(Set<String> problemJids);
    Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String problemAlias);

    interface ChapterProblemQueryBuilder extends QueryBuilder<ChapterProblemModel> {
        ChapterProblemQueryBuilder whereTypeIs(String type);
    }
}
