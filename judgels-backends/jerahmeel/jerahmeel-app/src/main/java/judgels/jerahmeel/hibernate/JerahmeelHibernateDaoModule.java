package judgels.jerahmeel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.jerahmeel.persistence.AdminRoleDao;

@Module
public class JerahmeelHibernateDaoModule {
    private JerahmeelHibernateDaoModule() {}

    @Provides
    static AdminRoleDao adminRoleDao(AdminRoleHibernateDao dao) {
        return dao;
    }
}
