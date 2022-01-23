package judgels.jophiel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import judgels.fs.aws.AwsModule;
import judgels.jophiel.auth.AuthModule;
import judgels.jophiel.hibernate.JophielHibernateBundle;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.session.SessionModule;
import judgels.jophiel.user.account.UserRegistrationModule;
import judgels.jophiel.user.account.UserResetPasswordModule;
import judgels.jophiel.user.avatar.UserAvatarModule;
import judgels.jophiel.user.registration.web.UserRegistrationWebConfig;
import judgels.jophiel.user.superadmin.SuperadminModule;
import judgels.jophiel.user.web.WebModule;
import judgels.recaptcha.RecaptchaModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.jaxrs.JudgelsObjectMappers;
import judgels.service.jersey.JudgelsJerseyFeature;

public class JophielApplication extends Application<JophielApplicationConfiguration> {
    private final HibernateBundle<JophielApplicationConfiguration> hibernateBundle = new JophielHibernateBundle();

    public static void main(String[] args) throws Exception {
        new JophielApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JophielApplicationConfiguration> bootstrap) {
        JudgelsObjectMappers.configure(bootstrap.getObjectMapper());

        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new JophielMigrationsBundle());
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(JophielApplicationConfiguration config, Environment env) {
        JophielConfiguration jophielConfig = config.getJophielConfig();
        Path baseDataDir = Paths.get(jophielConfig.getBaseDataDir());

        JophielComponent component = DaggerJophielComponent.builder()
                .authModule(new AuthModule(jophielConfig.getAuthConfig()))
                .awsModule(new AwsModule(jophielConfig.getAwsConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .mailerModule(new MailerModule(jophielConfig.getMailerConfig()))
                .recaptchaModule(new RecaptchaModule(jophielConfig.getRecaptchaConfig()))
                .userAvatarModule(new UserAvatarModule(baseDataDir, jophielConfig.getUserAvatarConfig()))
                .userRegistrationModule(new UserRegistrationModule(
                        jophielConfig.getUserRegistrationConfig(),
                        UserRegistrationWebConfig.fromServerConfig(jophielConfig)))
                .userResetPasswordModule(new UserResetPasswordModule(jophielConfig.getUserResetPasswordConfig()))
                .superadminModule(new SuperadminModule(jophielConfig.getSuperadminCreatorConfig()))
                .sessionModule(new SessionModule(jophielConfig.getSessionConfig()))
                .webModule(new WebModule(jophielConfig.getWebConfig()))
                .build();

        component.superadminCreator().ensureSuperadminExists();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.sessionResource());
        env.jersey().register(component.playSessionResource());
        env.jersey().register(component.legacyUserResource());
        env.jersey().register(component.myUserResource());
        env.jersey().register(component.profileResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.userAccountResource());
        env.jersey().register(component.userAvatarResource());
        env.jersey().register(component.userProfileResource());
        env.jersey().register(component.userRegistrationWebResource());
        env.jersey().register(component.userRatingResource());
        env.jersey().register(component.userSearchResource());
        env.jersey().register(component.userWebResource());
        env.jersey().register(component.clientUserResource());
        env.jersey().register(component.pingResource());

        component.scheduler().scheduleWithFixedDelay(
                "session-cleaner",
                component.sessionCleaner(),
                Duration.ofDays(1));
    }
}
