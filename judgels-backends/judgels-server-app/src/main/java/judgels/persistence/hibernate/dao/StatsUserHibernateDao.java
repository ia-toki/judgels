package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.StatsUserDao;
import judgels.persistence.model.StatsUserModel;
import judgels.persistence.model.StatsUserModel_;

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
