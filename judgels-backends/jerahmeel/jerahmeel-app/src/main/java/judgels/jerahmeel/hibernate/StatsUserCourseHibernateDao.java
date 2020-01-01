package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.StatsUserCourseDao;
import judgels.jerahmeel.persistence.StatsUserCourseModel;
import judgels.jerahmeel.persistence.StatsUserCourseModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserCourseHibernateDao extends HibernateDao<StatsUserCourseModel> implements StatsUserCourseDao {
    @Inject
    public StatsUserCourseHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<StatsUserCourseModel> selectByUserJidAndCourseJid(String userJid, String courseJid) {
        return selectByUniqueColumns(ImmutableMap.of(
                StatsUserCourseModel_.userJid, userJid,
                StatsUserCourseModel_.courseJid, courseJid));
    }
}
