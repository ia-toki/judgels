package judgels.jophiel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.AdminRoleDao;
import judgels.jophiel.persistence.AdminRoleModel;
import judgels.jophiel.persistence.AdminRoleModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class AdminRoleHibernateDao extends UnmodifiableHibernateDao<AdminRoleModel> implements AdminRoleDao {
    @Inject
    public AdminRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean isAdmin(String userJid) {
        return selectByUniqueColumn(AdminRoleModel_.userJid, userJid).isPresent();
    }
}
