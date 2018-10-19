package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.jophiel.role.AdminRoleStore;
import judgels.jophiel.role.RoleStore;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.avatar.UserAvatarIntegrationTestModule;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.jophiel.user.password.UserResetPasswordStore;
import judgels.jophiel.user.registration.UserRegistrationEmailStore;

@Component(modules = {
        JophielHibernateDaoModule.class,
        JophielIntegrationTestHibernateModule.class,
        JophielIntegrationTestPersistenceModule.class,
        UserAvatarIntegrationTestModule.class})
@Singleton
public interface JophielIntegrationTestComponent {
    SuperadminRoleStore superadminRoleStore();
    AdminRoleStore adminRoleStore();
    RoleStore roleStore();
    SessionStore sessionStore();
    UserStore userStore();
    UserInfoStore userInfoStore();
    UserRegistrationEmailStore userRegistrationEmailStore();
    UserResetPasswordStore userResetPasswordStore();
}
