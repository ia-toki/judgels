package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface StatsUserChapterDao extends Dao<StatsUserChapterModel> {
    Optional<StatsUserChapterModel> selectByUserJidAndChapterJid(String userJid, String chapterJid);
    List<StatsUserChapterModel> selectAllByUserJidAndChapterJids(String userJid, Set<String> chapterJids);
}
