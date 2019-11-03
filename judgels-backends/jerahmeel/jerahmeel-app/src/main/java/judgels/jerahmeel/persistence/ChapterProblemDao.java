package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ChapterProblemDao extends Dao<ChapterProblemModel> {
    Optional<ChapterProblemModel> selectByChapterJidAndProblemJid(String chapterJid, String problemJid);
    Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String lessonAlias);
    List<ChapterProblemModel> selectAllByChapterJid(String chapterJid, SelectionOptions options);
}
