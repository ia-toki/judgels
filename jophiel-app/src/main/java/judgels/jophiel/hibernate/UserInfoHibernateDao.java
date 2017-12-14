package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import judgels.jophiel.user.info.UserInfoDao;
import judgels.jophiel.user.info.UserInfoModel;
import judgels.jophiel.user.info.UserInfoModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class UserInfoHibernateDao extends HibernateDao<UserInfoModel> implements UserInfoDao {
    public UserInfoHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserInfoModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserInfoModel_.userJid, userJid);
    }
}
