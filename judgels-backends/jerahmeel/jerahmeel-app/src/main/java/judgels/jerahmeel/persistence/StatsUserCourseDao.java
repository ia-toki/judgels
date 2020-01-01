package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface StatsUserCourseDao extends Dao<StatsUserCourseModel> {
    Optional<StatsUserCourseModel> selectByUserJidAndCourseJid(String userJid, String courseJid);
}
