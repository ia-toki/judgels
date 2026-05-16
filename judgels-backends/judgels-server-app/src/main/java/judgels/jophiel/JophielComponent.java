package judgels.jophiel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.admin.user.UserAdminResource;
import judgels.admin.user.info.UserInfoAdminResource;
import judgels.admin.user.role.UserRoleAdminResource;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.mailer.MailerModule;
import judgels.profile.ProfileResource;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.session.SessionCleaner;
import judgels.session.SessionModule;
import judgels.session.SessionResource;
import judgels.user.UserResource;
import judgels.user.account.UserAccountResource;
import judgels.user.account.UserResetPasswordModule;
import judgels.user.avatar.UserAvatarModule;
import judgels.user.avatar.UserAvatarResource;
import judgels.user.info.UserInfoResource;
import judgels.user.rating.UserRatingResource;
import judgels.user.search.UserSearchResource;
import judgels.user.superadmin.SuperadminCreator;
import judgels.user.superadmin.SuperadminModule;
import judgels.user.web.UserWebResource;
import judgels.user.web.WebModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsServerModule.class,
        JudgelsPersistenceModule.class,
        JudgelsSchedulerModule.class,

        // Database
        JudgelsHibernateModule.class,
        JophielHibernateDaoModule.class,

        // 3rd parties
        MailerModule.class,

        // Features
        SuperadminModule.class,
        UserAvatarModule.class,
        UserResetPasswordModule.class,
        SessionModule.class,
        WebModule.class,

        judgels.contrib.auth.AuthModule.class,
        judgels.contrib.recaptcha.RecaptchaModule.class,
        judgels.contrib.user.registration.UserRegistrationModule.class})
@Singleton
public interface JophielComponent {
    ProfileResource profileResource();
    SessionResource sessionResource();
    SuperadminCreator superadminCreator();
    UserResource userResource();
    UserAdminResource userAdminResource();
    UserAccountResource userAccountResource();
    UserAvatarResource userAvatarResource();
    UserInfoResource userProfileResource();
    UserInfoAdminResource userInfoAdminResource();
    UserRatingResource userRatingResource();
    UserRoleAdminResource userRoleAdminResource();
    UserSearchResource userSearchResource();
    UserWebResource userWebResource();

    JudgelsScheduler scheduler();
    SessionCleaner sessionCleaner();

    judgels.contrib.session.SessionWithGoogleResource sessionWithGoogleResource();
    judgels.contrib.user.account.UserAccountWithRegistrationResource userAccountWithRegistrationResource();
    judgels.contrib.user.registration.web.UserRegistrationWebResource userRegistrationWebResource();
}
