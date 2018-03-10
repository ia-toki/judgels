package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.role.AdminRoleDao;
import judgels.uriel.role.AdminRoleModel;
import judgels.uriel.role.AdminRoleModel_;
import org.hibernate.SessionFactory;

@Singleton
public class AdminRoleHibernateDao extends UnmodifiableHibernateDao<AdminRoleModel> implements AdminRoleDao {
    @Inject
    public AdminRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByUserJid(String userJid) {
        return selectByUniqueColumn(AdminRoleModel_.userJid, userJid).isPresent();
    }
}
