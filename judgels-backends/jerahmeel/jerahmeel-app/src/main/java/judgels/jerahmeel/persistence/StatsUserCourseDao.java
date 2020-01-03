package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface StatsUserCourseDao extends Dao<StatsUserCourseModel> {
    Optional<StatsUserCourseModel> selectByUserJidAndCourseJid(String userJid, String courseJid);
    List<StatsUserCourseModel> selectAllByUserJidAndCourseJids(String userJid, Set<String> courseJid);
}
