package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserResetPasswordStore;
import judgels.jophiel.user.avatar.UserAvatarIntegrationTestModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        JophielModule.class,
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
