package judgels.jerahmeel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
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
        return selectByUniqueColumn(StatsUserModel_.userJid, userJid);
    }
}
