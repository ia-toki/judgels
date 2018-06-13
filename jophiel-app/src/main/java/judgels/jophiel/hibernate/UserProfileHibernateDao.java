package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserProfileDao;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.jophiel.persistence.UserProfileModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserProfileHibernateDao extends HibernateDao<UserProfileModel> implements UserProfileDao {
    @Inject
    public UserProfileHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserProfileModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserProfileModel_.userJid, userJid);
    }

    @Override
    public Map<String, UserProfileModel> selectAllByUserJids(Set<String> userJids) {
        return selectAll(new FilterOptions.Builder<UserProfileModel>()
                .putColumnsIn(UserProfileModel_.userJid, userJids)
                .build()).getData()
                .stream()
                .collect(Collectors.toMap(m -> m.userJid, m -> m));
    }
}
