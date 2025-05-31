package judgels;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import java.time.Duration;
import judgels.app.JudgelsApp;
import judgels.app.JudgelsAppEdition;
import judgels.jerahmeel.DaggerJerahmeelComponent;
import judgels.jerahmeel.JerahmeelComponent;
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jerahmeel.submission.bundle.ItemSubmissionModule;
import judgels.jophiel.DaggerJophielComponent;
import judgels.jophiel.JophielComponent;
import judgels.jophiel.JophielConfiguration;
import judgels.jophiel.auth.AuthModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.session.SessionModule;
import judgels.jophiel.user.account.UserResetPasswordModule;
import judgels.jophiel.user.avatar.UserAvatarModule;
import judgels.jophiel.user.superadmin.SuperadminModule;
import judgels.jophiel.user.web.WebModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.michael.DaggerMichaelComponent;
import judgels.michael.MichaelComponent;
import judgels.sandalphon.DaggerSandalphonComponent;
import judgels.sandalphon.SandalphonComponent;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.gabriel.GabrielClientModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.uriel.DaggerUrielComponent;
import judgels.uriel.UrielComponent;
import judgels.uriel.UrielConfiguration;
import judgels.uriel.file.FileModule;
import org.eclipse.jetty.server.session.SessionHandler;
import tlx.fs.aws.TlxAwsModule;
import tlx.jophiel.user.account.TlxUserRegistrationModule;
import tlx.jophiel.user.registration.web.UserRegistrationWebConfig;
import tlx.recaptcha.TlxRecaptchaModule;

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
        bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars", "/webjars", null, "webjars"));
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new JudgelsServerMigrationsBundle());
        bootstrap.addBundle(new JudgelsServerViewBundle());
        bootstrap.addBundle(new JudgelsServerWebSecurityBundle());
    }

    @Override
    public void run(JudgelsServerApplicationConfiguration config, Environment env) throws Exception {
        JudgelsApp.initialize(config.getJudgelsConfig().getAppConfig());

        runMichael(config, env);
        runJophiel(config, env);
        runSandalphon(config, env);
        runUriel(config, env);
        runJerahmeel(config, env);
    }

    private void runMichael(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        JophielConfiguration jophielConfig = config.getJophielConfig();
        SandalphonConfiguration sandalphonConfig = config.getSandalphonConfig();

        MichaelComponent component = DaggerMichaelComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(jophielConfig.getAuthConfig()))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gabrielClientModule(new GabrielClientModule(sandalphonConfig.getGabrielConfig()))
                .build();

        env.servlets().setSessionHandler(new SessionHandler());

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);
        env.jersey().register(component.indexResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.roleResource());
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

    private void runJophiel(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        JophielConfiguration jophielConfig = config.getJophielConfig();

        JophielComponent component = DaggerJophielComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(jophielConfig.getAuthConfig()))
                .mailerModule(new MailerModule(jophielConfig.getMailerConfig()))
                .superadminModule(new SuperadminModule(jophielConfig.getSuperadminCreatorConfig()))
                .userAvatarModule(new UserAvatarModule(jophielConfig.getUserAvatarConfig()))
                .userResetPasswordModule(new UserResetPasswordModule(jophielConfig.getUserResetPasswordConfig()))
                .sessionModule(new SessionModule(jophielConfig.getSessionConfig()))
                .webModule(new WebModule(jophielConfig.getWebConfig()))

                .tlxAwsModule(new TlxAwsModule(jophielConfig.getAwsConfig()))
                .tlxRecaptchaModule(new TlxRecaptchaModule(jophielConfig.getRecaptchaConfig()))
                .tlxUserRegistrationModule(new TlxUserRegistrationModule(
                        jophielConfig.getUserRegistrationConfig(),
                        UserRegistrationWebConfig.fromServerConfig(jophielConfig)))

                .build();

        component.superadminCreator().ensureSuperadminExists();

        env.jersey().register(component.sessionResource());
        env.jersey().register(component.profileResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.userAccountResource());
        env.jersey().register(component.userAvatarResource());
        env.jersey().register(component.userProfileResource());
        env.jersey().register(component.userRoleResource());
        env.jersey().register(component.userSearchResource());
        env.jersey().register(component.userWebResource());

        component.scheduler().scheduleWithFixedDelay(
                "jophiel-session-cleaner",
                component.sessionCleaner(),
                Duration.ofDays(1));

        if (JudgelsApp.getEdition() == JudgelsAppEdition.TLX) {
            env.jersey().register(component.tlxSessionResource());
            env.jersey().register(component.tlxUserRatingResource());
            env.jersey().register(component.tlxUserRegistrationWebResource());
        }
    }

    private void runSandalphon(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        SandalphonConfiguration sandalphonConfig = config.getSandalphonConfig();

        SandalphonComponent component = DaggerSandalphonComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gabrielClientModule(new GabrielClientModule(sandalphonConfig.getGabrielConfig()))
                .build();

        env.jersey().register(component.problemResource());
        env.jersey().register(component.lessonResource());

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "sandalphon-grading-response-poller",
                    component.gradingResponsePoller());
        }
    }

    private void runUriel(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        UrielConfiguration urielConfig = config.getUrielConfig();

        UrielComponent component = DaggerUrielComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gabrielClientModule(new GabrielClientModule(urielConfig.getGabrielConfig()))
                .fileModule(new FileModule(urielConfig.getFileConfig()))
                .submissionModule(new judgels.uriel.submission.programming.SubmissionModule(urielConfig.getSubmissionConfig()))

                .tlxAwsModule(new TlxAwsModule(urielConfig.getAwsConfig()))

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

        if (JudgelsApp.getEdition() == JudgelsAppEdition.TLX) {
            env.jersey().register(component.tlxContestRatingResource());
        }

        component.scheduler().scheduleWithFixedDelay(
                "uriel-contest-scoreboard-poller",
                component.contestScoreboardPoller(),
                Duration.ofSeconds(10));

        component.scheduler().scheduleWithFixedDelay(
                "uriel-contest-log-poller",
                component.contestLogPoller(),
                Duration.ofSeconds(3));

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "uriel-grading-response-poller",
                    component.gradingResponsePoller());
        }

        env.admin().addTask(component.dumpContestTask());

        if (JudgelsApp.getEdition() == JudgelsAppEdition.TLX) {
            env.admin().addTask(component.tlxReplaceProblemTask());
        }
    }

    private void runJerahmeel(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        JerahmeelConfiguration jerahmeelConfig = config.getJerahmeelConfig();

        JerahmeelComponent component = DaggerJerahmeelComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gabrielClientModule(new GabrielClientModule(jerahmeelConfig.getGabrielConfig()))
                .submissionModule(new judgels.jerahmeel.submission.programming.SubmissionModule(
                        jerahmeelConfig.getSubmissionConfig(),
                        jerahmeelConfig.getStatsConfig()))
                .itemSubmissionModule(new ItemSubmissionModule(jerahmeelConfig.getStatsConfig()))

                .tlxAwsModule(new TlxAwsModule(jerahmeelConfig.getAwsConfig()))

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

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "jerahmeel-grading-response-poller",
                    component.gradingResponsePoller());
        }

        env.admin().addTask(component.refreshContestStatsTask());
        env.admin().addTask(component.refreshProblemSetStatsTask());

        if (JudgelsApp.getEdition() == JudgelsAppEdition.TLX) {
            env.admin().addTask(component.tlxDeleteProblemTask());
            env.admin().addTask(component.tlxMoveProblemToChapterTask());
            env.admin().addTask(component.tlxMoveProblemToProblemSetTask());
            env.admin().addTask(component.tlxUploadDuplexSubmissionsToAwsTask());
        }
    }
}
