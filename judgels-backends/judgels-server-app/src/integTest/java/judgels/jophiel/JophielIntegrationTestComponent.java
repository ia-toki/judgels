package judgels.jophiel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.role.SuperadminRoleStore;
import judgels.service.JudgelsModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.session.SessionStore;
import judgels.user.UserStore;
import judgels.user.account.UserResetPasswordStore;
import judgels.user.avatar.UserAvatarIntegrationTestModule;

@Component(modules = {
        JophielHibernateDaoModule.class,
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        UserAvatarIntegrationTestModule.class})
@Singleton
public interface JophielIntegrationTestComponent {
    UserStore userStore();
    SessionStore sessionStore();
    SuperadminRoleStore superadminRoleStore();
    UserResetPasswordStore userResetPasswordStore();
}
