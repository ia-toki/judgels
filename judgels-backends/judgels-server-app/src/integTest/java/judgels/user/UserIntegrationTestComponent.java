package judgels.user;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.role.SuperadminRoleStore;
import judgels.service.JudgelsModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.service.persistence.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.hibernate.JudgelsServerHibernateDaoModule;
import judgels.session.SessionStore;
import judgels.user.account.UserResetPasswordStore;
import judgels.user.avatar.UserAvatarIntegrationTestModule;

@Component(modules = {
        JudgelsServerHibernateDaoModule.class,
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        UserAvatarIntegrationTestModule.class})
@Singleton
public interface UserIntegrationTestComponent {
    UserStore userStore();
    SessionStore sessionStore();
    SuperadminRoleStore superadminRoleStore();
    UserResetPasswordStore userResetPasswordStore();
}
