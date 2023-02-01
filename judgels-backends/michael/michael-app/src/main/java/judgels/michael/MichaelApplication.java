package judgels.michael;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.nio.file.Paths;
import java.time.Duration;
import judgels.fs.aws.AwsModule;
import judgels.jophiel.DaggerJophielComponent;
import judgels.jophiel.JophielComponent;
import judgels.jophiel.JophielConfiguration;
import judgels.jophiel.auth.AuthModule;
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
import judgels.service.JudgelsScheduler;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.jaxrs.JudgelsObjectMappers;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.uriel.DaggerUrielComponent;
import judgels.uriel.UrielComponent;
import judgels.uriel.UrielConfiguration;
import judgels.uriel.UrielModule;
import judgels.uriel.file.FileModule;

public class MichaelApplication extends Application<MichaelApplicationConfiguration> {
    private final HibernateBundle<MichaelApplicationConfiguration> hibernateBundle = new MichaelHibernateBundle();

    public static void main(String[] args) throws Exception {
        new MichaelApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<MichaelApplicationConfiguration> bootstrap) {
        JudgelsObjectMappers.configure(bootstrap.getObjectMapper());

        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new MichaelMigrationsBundle());
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(MichaelApplicationConfiguration config, Environment env) {
        MichaelComponent component = DaggerMichaelComponent.builder()
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);
        env.jersey().register(component.pingResource());

        runJophiel(config.getJophielConfig(), env, component.scheduler());
        runUriel(config.getUrielConfig(), env, component.scheduler());
    }

    private void runJophiel(JophielConfiguration config, Environment env, JudgelsScheduler scheduler) {
        JophielComponent component = DaggerJophielComponent.builder()
                .authModule(new AuthModule(config.getAuthConfig()))
                .awsModule(new AwsModule(config.getAwsConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .mailerModule(new MailerModule(config.getMailerConfig()))
                .recaptchaModule(new RecaptchaModule(config.getRecaptchaConfig()))
                .userAvatarModule(new UserAvatarModule(
                        Paths.get(config.getBaseDataDir()),
                        config.getUserAvatarConfig()))
                .userRegistrationModule(new UserRegistrationModule(
                        config.getUserRegistrationConfig(),
                        UserRegistrationWebConfig.fromServerConfig(config)))
                .userResetPasswordModule(new UserResetPasswordModule(config.getUserResetPasswordConfig()))
                .superadminModule(new SuperadminModule(config.getSuperadminCreatorConfig()))
                .sessionModule(new SessionModule(config.getSessionConfig()))
                .webModule(new WebModule(config.getWebConfig()))
                .build();

        component.superadminCreator().ensureSuperadminExists();

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

        scheduler.scheduleWithFixedDelay(
                "session-cleaner",
                component.sessionCleaner(),
                Duration.ofDays(1));
    }

    private void runUriel(UrielConfiguration config, Environment env, JudgelsScheduler scheduler) {
        UrielComponent component = DaggerUrielComponent.builder()
                .awsModule(new AwsModule(config.getAwsConfig()))
                .fileModule(new FileModule(config.getFileConfig()))
                .jophielModule(new judgels.uriel.jophiel.JophielModule(config.getJophielConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new judgels.uriel.sandalphon.SandalphonModule(config.getSandalphonConfig()))
                .gabrielModule(new judgels.uriel.gabriel.GabrielModule(config.getGabrielConfig()))
                .messagingModule(new judgels.uriel.messaging.MessagingModule(config.getRabbitMQConfig()))
                .submissionModule(new judgels.uriel.submission.programming.SubmissionModule(config.getSubmissionConfig()))
                .urielModule(new UrielModule(config))
                .build();

        env.jersey().register(component.contestResource());
        env.jersey().register(component.contestWebResource());
        env.jersey().register(component.contestAnnouncementResource());
        env.jersey().register(component.contestClarificationResource());
        env.jersey().register(component.contestContestantResource());
        env.jersey().register(component.contestEditorialResource());
        env.jersey().register(component.contestFileResource());
        env.jersey().register(component.contestHistoryResource());
        env.jersey().register(component.contestLogResource());
        env.jersey().register(component.contestManagerResource());
        env.jersey().register(component.contestModuleResource());
        env.jersey().register(component.contestProblemResource());
        env.jersey().register(component.contestScoreboardResource());
        env.jersey().register(component.contestProgrammingSubmissionResource());
        env.jersey().register(component.contestBundleSubmissionResource());
        env.jersey().register(component.contestSupervisorResource());
        env.jersey().register(component.contestRatingResource());

        scheduler.scheduleWithFixedDelay(
                "contest-scoreboard-poller",
                component.contestScoreboardPoller(),
                Duration.ofSeconds(10));

        scheduler.scheduleOnce(
                "contest-log-poller",
                component.contestLogPoller());

        if (config.getRabbitMQConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "grading-response-poller",
                    component.gradingResponsePoller());
        }
    }
}
