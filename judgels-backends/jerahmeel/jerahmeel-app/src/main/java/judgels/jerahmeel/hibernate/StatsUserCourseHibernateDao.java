package judgels.jerahmeel.hibernate;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.StatsUserCourseDao;
import judgels.jerahmeel.persistence.StatsUserCourseModel;
import judgels.jerahmeel.persistence.StatsUserCourseModel_;
import judgels.persistence.FilterOptions;
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

    @Override
    public List<StatsUserCourseModel> selectAllByUserJidAndCourseJids(String userJid, Set<String> courseJids) {
        return selectAll(new FilterOptions.Builder<StatsUserCourseModel>()
                .putColumnsEq(StatsUserCourseModel_.userJid, userJid)
                .putColumnsIn(StatsUserCourseModel_.courseJid, courseJids)
                .build());
    }
}
