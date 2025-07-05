package judgels.jophiel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserInfoDao;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserResetPasswordDao;
import judgels.jophiel.persistence.UserRoleDao;

@Module
public class JophielHibernateDaoModule {
    private JophielHibernateDaoModule() {}

    @Provides
    static SessionDao sessionDao(SessionHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserDao userDao(UserHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserInfoDao userInfoDao(UserInfoHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRatingDao userRatingDao(UserRatingHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserResetPasswordDao userResetPasswordDao(UserResetPasswordHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRoleDao userRoleDao(UserRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static judgels.contrib.jophiel.persistence.UserRatingEventDao userRatingEventDao(
            judgels.contrib.jophiel.hibernate.UserRatingEventHibernateDao dao) {

        return dao;
    }

    @Provides
    static judgels.contrib.jophiel.persistence.UserRegistrationEmailDao userRegistrationEmailDao(
            judgels.contrib.jophiel.hibernate.UserRegistrationEmailHibernateDao dao) {

        return dao;
    }
}
