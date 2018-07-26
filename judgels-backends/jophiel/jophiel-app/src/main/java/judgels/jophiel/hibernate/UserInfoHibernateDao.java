package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserInfoDao;
import judgels.jophiel.persistence.UserInfoModel;
import judgels.jophiel.persistence.UserInfoModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserInfoHibernateDao extends HibernateDao<UserInfoModel> implements UserInfoDao {
    @Inject
    public UserInfoHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserInfoModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserInfoModel_.userJid, userJid);
    }

    @Override
    public Map<String, UserInfoModel> selectAllByUserJids(Set<String> userJids) {
        return selectAll(new FilterOptions.Builder<UserInfoModel>()
                .putColumnsIn(UserInfoModel_.userJid, userJids)
                .build())
                .stream()
                .collect(Collectors.toMap(m -> m.userJid, m -> m));
    }
}
