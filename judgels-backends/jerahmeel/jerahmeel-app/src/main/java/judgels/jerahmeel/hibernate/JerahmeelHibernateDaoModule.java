package judgels.jerahmeel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.jerahmeel.persistence.AdminRoleDao;
import judgels.jerahmeel.persistence.CourseDao;

@Module
public class JerahmeelHibernateDaoModule {
    private JerahmeelHibernateDaoModule() {}

    @Provides
    static AdminRoleDao adminRoleDao(AdminRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static CourseDao courseDao(CourseHibernateDao dao) {
        return dao;
    }
}
