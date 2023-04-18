package judgels;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.bundles.webjars.WebJarBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.nio.file.Paths;
import java.time.Duration;
import judgels.fs.aws.AwsModule;
import judgels.jerahmeel.DaggerJerahmeelComponent;
import judgels.jerahmeel.JerahmeelComponent;
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jerahmeel.JerahmeelModule;
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
import judgels.michael.DaggerMichaelComponent;
import judgels.michael.MichaelComponent;
import judgels.michael.MichaelModule;
import judgels.recaptcha.RecaptchaModule;
import judgels.sandalphon.DaggerSandalphonComponent;
import judgels.sandalphon.SandalphonComponent;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sandalphon.SandalphonModule;
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
import org.eclipse.jetty.server.session.SessionHandler;

public class JudgelsServerApplication extends Application<JudgelsServerApplicationConfiguration> {
    private final HibernateBundle<JudgelsServerApplicationConfiguration> hibernateBundle = new JudgelsServerHibernateBundle();

    public static void main(String[] args) throws Exception {
        new JudgelsServerApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JudgelsServerApplicationConfiguration> bootstrap) {
        JudgelsObjectMappers.configure(bootstrap.getObjectMapper());

        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new JudgelsServerMigrationsBundle());
        bootstrap.addBundle(new JudgelsServerViewBundle());
        bootstrap.addBundle(new WebJarBundle("org.webjars.bower", "org.webjars.npm"));
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(JudgelsServerApplicationConfiguration config, Environment env) throws Exception {
        JudgelsScheduler scheduler = new JudgelsScheduler(env.lifecycle());

        runMichael(config, env, scheduler);
        runJophiel(config, env, scheduler);
        runSandalphon(config, env, scheduler);
        runUriel(config, env, scheduler);
        runJerahmeel(config, env, scheduler);
    }

    private void runMichael(JudgelsServerApplicationConfiguration config, Environment env, JudgelsScheduler scheduler) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        JophielConfiguration jophielConfig = config.getJophielConfig();
        SandalphonConfiguration sandalphonConfig = config.getSandalphonConfig();

        MichaelComponent component = DaggerMichaelComponent.builder()
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .michaelModule(new MichaelModule(judgelsConfig.getAppConfig()))

                // Jophiel
                .awsModule(new AwsModule(jophielConfig.getAwsConfig()))
                .userAvatarModule(new UserAvatarModule(
                        Paths.get(jophielConfig.getBaseDataDir()),
                        jophielConfig.getUserAvatarConfig()))

                // Sandalphon
                .sandalphonModule(new SandalphonModule(sandalphonConfig))

                .build();

        env.servlets().setSessionHandler(new SessionHandler());

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);
        env.jersey().register(component.pingResource());
        env.jersey().register(component.indexResource());
        env.jersey().register(component.problemResource());
        env.jersey().register(component.problemStatementResource());
        env.jersey().register(component.problemStatementRenderResourceInEditProblemStatement());
        env.jersey().register(component.problemStatementRenderResourceInViewProgrammingProblemStatement());
        env.jersey().register(component.problemStatementRenderResourceInViewBundleProblemStatement());
        env.jersey().register(component.problemPartnerResource());
        env.jersey().register(component.problemEditorialResource());
        env.jersey().register(component.problemEditorialRenderResourceInEditProblemEditorial());
        env.jersey().register(component.problemEditorialRenderResourceInViewProblemEditorial());
        env.jersey().register(component.problemVersionResource());
        env.jersey().register(component.programmingProblemStatementResource());
        env.jersey().register(component.programmingProblemGradingResource());
        env.jersey().register(component.programmingProblemSubmissionResource());
        env.jersey().register(component.bundleProblemStatementResource());
        env.jersey().register(component.bundleProblemItemResource());
        env.jersey().register(component.bundleProblemSubmissionResource());
        env.jersey().register(component.lessonResource());
        env.jersey().register(component.lessonStatementResource());
        env.jersey().register(component.lessonStatementRenderResourceInEditLessonStatement());
        env.jersey().register(component.lessonStatementRenderResourceInViewLessonStatement());
        env.jersey().register(component.lessonPartnerResource());
        env.jersey().register(component.lessonVersionResource());
    }

    private void runJophiel(JudgelsServerApplicationConfiguration config, Environment env, JudgelsScheduler scheduler) {
        JophielConfiguration jophielConfig = config.getJophielConfig();

        JophielComponent component = DaggerJophielComponent.builder()
                .authModule(new AuthModule(jophielConfig.getAuthConfig()))
                .awsModule(new AwsModule(jophielConfig.getAwsConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .mailerModule(new MailerModule(jophielConfig.getMailerConfig()))
                .recaptchaModule(new RecaptchaModule(jophielConfig.getRecaptchaConfig()))
                .userAvatarModule(new UserAvatarModule(
                        Paths.get(jophielConfig.getBaseDataDir()),
                        jophielConfig.getUserAvatarConfig()))
                .userRegistrationModule(new UserRegistrationModule(
                        jophielConfig.getUserRegistrationConfig(),
                        UserRegistrationWebConfig.fromServerConfig(jophielConfig)))
                .userResetPasswordModule(new UserResetPasswordModule(jophielConfig.getUserResetPasswordConfig()))
                .superadminModule(new SuperadminModule(jophielConfig.getSuperadminCreatorConfig()))
                .sessionModule(new SessionModule(jophielConfig.getSessionConfig()))
                .webModule(new WebModule(jophielConfig.getWebConfig()))
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
                "jophiel-session-cleaner",
                component.sessionCleaner(),
                Duration.ofDays(1));
    }

    private void runSandalphon(JudgelsServerApplicationConfiguration config, Environment env, JudgelsScheduler scheduler) {
        SandalphonConfiguration sandalphonConfig = config.getSandalphonConfig();

        SandalphonComponent component = DaggerSandalphonComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new SandalphonModule(sandalphonConfig))
                .build();

        env.jersey().register(component.problemResource());
        env.jersey().register(component.lessonResource());
    }

    private void runUriel(JudgelsServerApplicationConfiguration config, Environment env, JudgelsScheduler scheduler) {
        JophielConfiguration jophielConfig = config.getJophielConfig();
        UrielConfiguration urielConfig = config.getUrielConfig();

        UrielComponent component = DaggerUrielComponent.builder()
                .userAvatarModule(new UserAvatarModule(
                        Paths.get(jophielConfig.getBaseDataDir()),
                        jophielConfig.getUserAvatarConfig()))
                .awsModule(new AwsModule(urielConfig.getAwsConfig()))
                .fileModule(new FileModule(urielConfig.getFileConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new judgels.uriel.sandalphon.SandalphonModule(urielConfig.getSandalphonConfig()))
                .gabrielModule(new judgels.uriel.gabriel.GabrielModule(urielConfig.getGabrielConfig()))
                .messagingModule(new judgels.uriel.messaging.MessagingModule(urielConfig.getRabbitMQConfig()))
                .submissionModule(new judgels.uriel.submission.programming.SubmissionModule(urielConfig.getSubmissionConfig()))
                .urielModule(new UrielModule(urielConfig))
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
                "uriel-contest-scoreboard-poller",
                component.contestScoreboardPoller(),
                Duration.ofSeconds(10));

        scheduler.scheduleWithFixedDelay(
                "uriel-contest-log-poller",
                component.contestLogPoller(),
                Duration.ofSeconds(3));

        if (urielConfig.getRabbitMQConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "uriel-grading-response-poller",
                    component.gradingResponsePoller());
        }
    }
    private void runJerahmeel(JudgelsServerApplicationConfiguration config, Environment env, JudgelsScheduler scheduler) {
        JophielConfiguration jophielConfig = config.getJophielConfig();
        JerahmeelConfiguration jerahmeelConfig = config.getJerahmeelConfig();

        JerahmeelComponent component = DaggerJerahmeelComponent.builder()
                .userAvatarModule(new UserAvatarModule(
                        Paths.get(jophielConfig.getBaseDataDir()),
                        jophielConfig.getUserAvatarConfig()))
                .awsModule(new AwsModule(jerahmeelConfig.getAwsConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new judgels.jerahmeel.sandalphon.SandalphonModule(jerahmeelConfig.getSandalphonConfig()))
                .urielModule(new judgels.jerahmeel.uriel.UrielModule(jerahmeelConfig.getUrielConfig()))
                .gabrielModule(new judgels.jerahmeel.gabriel.GabrielModule(jerahmeelConfig.getGabrielConfig()))
                .messagingModule(new judgels.jerahmeel.messaging.MessagingModule(jerahmeelConfig.getRabbitMQConfig()))
                .submissionModule(new judgels.jerahmeel.submission.programming.SubmissionModule(
                        jerahmeelConfig.getSubmissionConfig(),
                        jerahmeelConfig.getStatsConfig()))
                .jerahmeelModule(new JerahmeelModule(jerahmeelConfig))
                .build();

        env.jersey().register(component.archiveResource());
        env.jersey().register(component.curriculumResource());
        env.jersey().register(component.courseResource());
        env.jersey().register(component.chapterResource());
        env.jersey().register(component.courseChapterResource());
        env.jersey().register(component.chapterLessonResource());
        env.jersey().register(component.chapterProblemResource());
        env.jersey().register(component.problemResource());
        env.jersey().register(component.problemTagResource());
        env.jersey().register(component.problemSetResource());
        env.jersey().register(component.problemSetProblemResource());
        env.jersey().register(component.itemSubmissionResource());
        env.jersey().register(component.submissionResource());
        env.jersey().register(component.userStatsResource());

        if (jerahmeelConfig.getRabbitMQConfig().isPresent()) {
            scheduler.scheduleOnce(
                    "jerahmeel-grading-response-poller",
                    component.gradingResponsePoller());
        }

        env.admin().addTask(component.problemSetStatsTask());
        env.admin().addTask(component.contestStatsTask());
    }
}
