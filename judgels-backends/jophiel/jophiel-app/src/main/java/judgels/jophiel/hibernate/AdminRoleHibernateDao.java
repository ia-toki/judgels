package judgels.jophiel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.AdminRoleDao;
import judgels.jophiel.persistence.AdminRoleModel;
import judgels.jophiel.persistence.AdminRoleModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

@Singleton
public class AdminRoleHibernateDao extends UnmodifiableHibernateDao<AdminRoleModel> implements AdminRoleDao {
    @Inject
    public AdminRoleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean isAdmin(String userJid) {
        return selectByUniqueColumn(AdminRoleModel_.userJid, userJid).isPresent();
    }
}
