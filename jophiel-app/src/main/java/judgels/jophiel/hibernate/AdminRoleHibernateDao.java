package judgels.jophiel.hibernate;

import java.time.Clock;
import judgels.jophiel.role.AdminRoleDao;
import judgels.jophiel.role.AdminRoleModel;
import judgels.jophiel.role.AdminRoleModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

public class AdminRoleHibernateDao extends UnmodifiableHibernateDao<AdminRoleModel> implements AdminRoleDao {
    public AdminRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByUserJid(String userJid) {
        return selectByUniqueColumn(AdminRoleModel_.userJid, userJid).isPresent();
    }
}
