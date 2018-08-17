package judgels.jophiel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.nio.file.Path;
import java.nio.file.Paths;
import judgels.fs.aws.AwsModule;
import judgels.jophiel.hibernate.JophielHibernateBundle;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.user.avatar.UserAvatarModule;
import judgels.jophiel.user.password.UserResetPasswordModule;
import judgels.jophiel.user.registration.UserRegistrationModule;
import judgels.jophiel.user.registration.web.UserRegistrationWebConfig;
import judgels.recaptcha.RecaptchaModule;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.service.jersey.JudgelsObjectMappers;

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
                .awsModule(new AwsModule(jophielConfig.getAwsConfig()))
                .jophielHibernateModule(new JophielHibernateModule(hibernateBundle))
                .mailerModule(new MailerModule(jophielConfig.getMailerConfig()))
                .recaptchaModule(new RecaptchaModule(jophielConfig.getRecaptchaConfig()))
                .userAvatarModule(new UserAvatarModule(baseDataDir, jophielConfig.getUserAvatarConfig()))
                .userRegistrationModule(new UserRegistrationModule(
                        jophielConfig.getUserRegistrationConfig(),
                        UserRegistrationWebConfig.fromServerConfig(jophielConfig)))
                .userResetPasswordModule(new UserResetPasswordModule(jophielConfig.getUserResetPasswordConfig()))
                .build();

        component.superadminCreator().create();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.sessionResource());
        env.jersey().register(component.legacySessionResource());
        env.jersey().register(component.legacyUserResource());
        env.jersey().register(component.myResource());
        env.jersey().register(component.profileResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.userAccountResource());
        env.jersey().register(component.userAvatarResource());
        env.jersey().register(component.userProfileResource());
        env.jersey().register(component.userRegistrationWebResource());
        env.jersey().register(component.userRatingResource());
        env.jersey().register(component.webResource());
        env.jersey().register(component.versionResource());
    }
}
