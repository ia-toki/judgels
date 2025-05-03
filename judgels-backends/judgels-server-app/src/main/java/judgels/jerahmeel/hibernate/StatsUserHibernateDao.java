package judgels.jerahmeel.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserModel;
import judgels.jerahmeel.persistence.StatsUserModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class StatsUserHibernateDao extends HibernateDao<StatsUserModel> implements StatsUserDao {
    @Inject
    public StatsUserHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<StatsUserModel> selectByUserJid(String userJid) {
        return select().where(columnEq(StatsUserModel_.userJid, userJid)).unique();
    }
}
