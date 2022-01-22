package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ChapterProblemDao extends Dao<ChapterProblemModel> {
    Optional<ChapterProblemModel> selectByProblemJid(String problemJid);
    List<ChapterProblemModel> selectAllByProblemJids(Set<String> problemJids);
    Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String lessonAlias);
    List<ChapterProblemModel> selectAllByChapterJid(String chapterJid, SelectionOptions options);
    List<ChapterProblemModel> selectAllBundleByChapterJid(String chapterJid, SelectionOptions options);
    List<ChapterProblemModel> selectAllProgrammingByChapterJid(String chapterJid, SelectionOptions options);
    List<ChapterProblemModel> selectAllProgrammingByChapterJids(Set<String> chapterJids);
    int selectCountProgrammingByChapterJid(String chapterJid);
}
