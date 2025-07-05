package judgels.jophiel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.profile.ProfileResource;
import judgels.jophiel.session.SessionCleaner;
import judgels.jophiel.session.SessionModule;
import judgels.jophiel.session.SessionResource;
import judgels.jophiel.user.UserResource;
import judgels.jophiel.user.account.UserAccountResource;
import judgels.jophiel.user.account.UserResetPasswordModule;
import judgels.jophiel.user.avatar.UserAvatarModule;
import judgels.jophiel.user.avatar.UserAvatarResource;
import judgels.jophiel.user.info.UserInfoResource;
import judgels.jophiel.user.role.UserRoleResource;
import judgels.jophiel.user.search.UserSearchResource;
import judgels.jophiel.user.superadmin.SuperadminCreator;
import judgels.jophiel.user.superadmin.SuperadminModule;
import judgels.jophiel.user.web.UserWebResource;
import judgels.jophiel.user.web.WebModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;

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

        judgels.contrib.jophiel.auth.AuthModule.class,
        judgels.contrib.jophiel.recaptcha.RecaptchaModule.class,
        judgels.contrib.jophiel.user.registration.UserRegistrationModule.class})
@Singleton
public interface JophielComponent {
    ProfileResource profileResource();
    SessionResource sessionResource();
    SuperadminCreator superadminCreator();
    UserResource userResource();
    UserAccountResource userAccountResource();
    UserAvatarResource userAvatarResource();
    UserInfoResource userProfileResource();
    UserRoleResource userRoleResource();
    UserSearchResource userSearchResource();
    UserWebResource userWebResource();

    JudgelsScheduler scheduler();
    SessionCleaner sessionCleaner();

    judgels.contrib.jophiel.session.SessionWithGoogleResource sessionWithGoogleResource();
    judgels.contrib.jophiel.user.account.UserAccountWithRegistrationResource userAccountWithRegistrationResource();
    judgels.contrib.jophiel.user.rating.UserRatingResource userRatingResource();
    judgels.contrib.jophiel.user.registration.web.UserRegistrationWebResource userRegistrationWebResource();
}
