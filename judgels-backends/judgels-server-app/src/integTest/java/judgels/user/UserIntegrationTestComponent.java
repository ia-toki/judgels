package judgels.user;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.core.JudgelsModule;
import judgels.persistence.JudgelsHibernateModule;
import judgels.persistence.JudgelsPersistenceModule;
import judgels.role.SuperadminRoleStore;
import judgels.session.SessionStore;
import judgels.user.account.UserResetPasswordStore;
import judgels.user.avatar.UserAvatarIntegrationTestModule;

@Component(modules = {
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
