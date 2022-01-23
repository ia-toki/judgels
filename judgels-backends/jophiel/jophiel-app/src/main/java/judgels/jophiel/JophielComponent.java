package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.fs.aws.AwsModule;
import judgels.jophiel.auth.AuthModule;
import judgels.jophiel.client.user.ClientUserResource;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.jophiel.legacy.user.LegacyUserResource;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.play.PlaySessionResource;
import judgels.jophiel.profile.ProfileResource;
import judgels.jophiel.session.SessionCleaner;
import judgels.jophiel.session.SessionModule;
import judgels.jophiel.session.SessionResource;
import judgels.jophiel.user.UserResource;
import judgels.jophiel.user.account.UserAccountResource;
import judgels.jophiel.user.account.UserRegistrationModule;
import judgels.jophiel.user.account.UserResetPasswordModule;
import judgels.jophiel.user.avatar.UserAvatarModule;
import judgels.jophiel.user.avatar.UserAvatarResource;
import judgels.jophiel.user.info.UserInfoResource;
import judgels.jophiel.user.me.MyUserResource;
import judgels.jophiel.user.rating.UserRatingResource;
import judgels.jophiel.user.registration.web.UserRegistrationWebResource;
import judgels.jophiel.user.search.UserSearchResource;
import judgels.jophiel.user.superadmin.SuperadminCreator;
import judgels.jophiel.user.superadmin.SuperadminModule;
import judgels.jophiel.user.web.UserWebResource;
import judgels.jophiel.user.web.WebModule;
import judgels.recaptcha.RecaptchaModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.JudgelsScheduler;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        AuthModule.class,
        AwsModule.class,
        JophielModule.class,
        JophielHibernateDaoModule.class,
        JudgelsModule.class,
        JudgelsApplicationModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        MailerModule.class,
        RecaptchaModule.class,
        SuperadminModule.class,
        UserAvatarModule.class,
        UserRegistrationModule.class,
        UserResetPasswordModule.class,
        SessionModule.class,
        WebModule.class})
@Singleton
public interface JophielComponent {
    LegacyUserResource legacyUserResource();
    PlaySessionResource playSessionResource();
    MyUserResource myUserResource();
    ProfileResource profileResource();
    SessionResource sessionResource();
    SuperadminCreator superadminCreator();
    UserResource userResource();
    UserAccountResource userAccountResource();
    UserAvatarResource userAvatarResource();
    UserInfoResource userProfileResource();
    UserRegistrationWebResource userRegistrationWebResource();
    UserRatingResource userRatingResource();
    UserSearchResource userSearchResource();
    UserWebResource userWebResource();
    ClientUserResource clientUserResource();
    PingResource pingResource();

    JudgelsScheduler scheduler();
    SessionCleaner sessionCleaner();
}
