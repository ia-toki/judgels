package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.StatsUserDao;
import judgels.persistence.StatsUserModel;
import judgels.persistence.StatsUserModel_;
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
