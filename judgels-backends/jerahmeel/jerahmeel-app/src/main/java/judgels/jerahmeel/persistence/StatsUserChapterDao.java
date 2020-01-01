package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface StatsUserChapterDao extends Dao<StatsUserChapterModel> {
    Optional<StatsUserChapterModel> selectByUserJidAndChapterJid(String userJid, String chapterJid);
}
