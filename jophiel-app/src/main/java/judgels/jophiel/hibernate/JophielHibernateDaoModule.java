package judgels.jophiel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.jophiel.legacy.session.LegacySessionDao;
import judgels.jophiel.legacy.session.LegacySessionHibernateDao;
import judgels.jophiel.persistence.AdminRoleDao;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserProfileDao;
import judgels.jophiel.persistence.UserRegistrationEmailDao;
import judgels.jophiel.persistence.UserResetPasswordDao;

@Module
public class JophielHibernateDaoModule {
    private JophielHibernateDaoModule() {}

    @Provides
    static AdminRoleDao adminRoleDao(AdminRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static LegacySessionDao legacySessionDao(LegacySessionHibernateDao dao) {
        return dao;
    }

    @Provides
    static SessionDao sessionDao(SessionHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserDao userDao(UserHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserProfileDao userProfileDao(UserProfileHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRegistrationEmailDao userRegistrationEmailDao(UserRegistrationEmailHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserResetPasswordDao userResetPasswordDao(UserResetPasswordHibernateDao dao) {
        return dao;
    }
}
