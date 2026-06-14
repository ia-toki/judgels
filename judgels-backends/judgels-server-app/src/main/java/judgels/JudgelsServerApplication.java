package judgels;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import java.time.Duration;
import judgels.app.JudgelsApp;
import judgels.grading.GradingModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.michael.DaggerMichaelComponent;
import judgels.michael.MichaelComponent;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.service.persistence.hibernate.JudgelsHibernateModule;
import judgels.session.SessionModule;
import judgels.user.superadmin.SuperadminModule;
import judgels.user.web.WebModule;
import org.eclipse.jetty.server.session.SessionHandler;
import tlx.TlxServerComponent;
import tlx.auth.AuthModule;
import tlx.fs.aws.AwsConfiguration;
import tlx.fs.aws.AwsFileSystem;
import tlx.fs.aws.AwsFsConfiguration;
import tlx.mailer.MailerModule;
import tlx.recaptcha.RecaptchaModule;
import tlx.training.TrainingConfiguration;
import tlx.training.submission.bundle.TrainingItemSubmissionModule;
import tlx.training.submission.programming.TrainingSubmissionModule;
import tlx.user.account.UserResetPasswordModule;
import tlx.user.registration.UserRegistrationModule;
import tlx.user.registration.web.UserRegistrationWebConfig;

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
        JudgelsServerComponent serverComponent = runJudgelsServer(config, env);

        if (JudgelsApp.isTLX()) {
            runTlxServer(serverComponent, config, env);
        }
    }

    private void runMichael(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();

        MichaelComponent component = DaggerMichaelComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(judgelsConfig.getAuthConfig()))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gradingModule(new GradingModule(judgelsConfig.getGradingConfig()))
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
        env.jersey().register(component.problemStatementRenderResourceInViewBundleItemProblemStatement());
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

    private JudgelsServerComponent runJudgelsServer(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();

        JudgelsServerComponent component = DaggerJudgelsServerComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(judgelsConfig.getAuthConfig()))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gradingModule(new GradingModule(judgelsConfig.getGradingConfig()))
                .superadminModule(new SuperadminModule(judgelsConfig.getSuperadminCreatorConfig()))
                .sessionModule(new SessionModule(judgelsConfig.getSessionConfig()))
                .webModule(new WebModule(judgelsConfig.getWebConfig()))
                .build();

        component.superadminCreator().ensureSuperadminExists();

        env.jersey().register(component.sessionResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.userAvatarResource());
        env.jersey().register(component.userProfileResource());
        env.jersey().register(component.userRatingResource());
        env.jersey().register(component.userRoleResource());
        env.jersey().register(component.userSearchResource());
        env.jersey().register(component.userWebResource());
        env.jersey().register(component.profileResource());

        env.jersey().register(component.problemResource());
        env.jersey().register(component.problemTagResource());
        env.jersey().register(component.lessonResource());

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
        env.jersey().register(component.contestSubmissionResource());
        env.jersey().register(component.contestItemSubmissionResource());
        env.jersey().register(component.contestSupervisorResource());

        env.jersey().register(component.settingResource());

        component.scheduler().scheduleWithFixedDelay(
                "session-cleaner",
                component.sessionCleaner(),
                Duration.ofDays(1));

        component.scheduler().scheduleWithFixedDelay(
                "contest-scoreboard-poller",
                component.contestScoreboardPoller(),
                Duration.ofSeconds(10));

        component.scheduler().scheduleWithFixedDelay(
                "contest-log-poller",
                component.contestLogPoller(),
                Duration.ofSeconds(3));

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            env.lifecycle().manage(component.problemGradingResponsePoller());
            env.lifecycle().manage(component.contestGradingResponsePoller());
        }

        env.admin().addTask(component.dumpContestTask());

        return component;
    }

    private void runTlxServer(
            JudgelsServerComponent serverComponent,
            JudgelsServerApplicationConfiguration config,
            Environment env) {

        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();
        TrainingConfiguration trainingConfig = config.getTrainingConfig().get();

        TrainingSubmissionModule trainingSubmissionModule =
                new TrainingSubmissionModule(trainingConfig.getStatsConfig());
        if (trainingConfig.getSubmissionConfig().isPresent()
                && trainingConfig.getAwsConfig().isPresent()
                && trainingConfig.getSubmissionConfig().get().getFs() instanceof AwsFsConfiguration) {
            AwsConfiguration awsConfig = trainingConfig.getAwsConfig().get();
            AwsFsConfiguration submissionFsConfig = (AwsFsConfiguration) trainingConfig.getSubmissionConfig().get().getFs();
            AwsFileSystem submissionFs = new AwsFileSystem(awsConfig, submissionFsConfig);
            trainingSubmissionModule = new TrainingSubmissionModule(trainingConfig.getStatsConfig(), submissionFs);
        }

        TlxServerComponent component = serverComponent.tlxServerComponentFactory().create(
                new MailerModule(judgelsConfig.getMailerConfig()),
                new RecaptchaModule(judgelsConfig.getRecaptchaConfig()),
                new UserRegistrationModule(UserRegistrationWebConfig.fromServerConfig(judgelsConfig)),
                new UserResetPasswordModule(judgelsConfig.getUserResetPasswordConfig()),
                trainingSubmissionModule,
                new TrainingItemSubmissionModule(trainingConfig.getStatsConfig()));

        env.jersey().register(component.sessionResource());
        env.jersey().register(component.userAccountResource());
        env.jersey().register(component.userRegistrationWebResource());
        env.jersey().register(component.userRatingResource());

        env.jersey().register(component.contestRatingResource());

        env.jersey().register(component.archiveResource());
        env.jersey().register(component.curriculumResource());
        env.jersey().register(component.courseResource());
        env.jersey().register(component.chapterResource());
        env.jersey().register(component.courseChapterResource());
        env.jersey().register(component.chapterLessonResource());
        env.jersey().register(component.chapterProblemResource());
        env.jersey().register(component.problemResource());
        env.jersey().register(component.problemSetResource());
        env.jersey().register(component.problemSetProblemResource());
        env.jersey().register(component.itemSubmissionResource());
        env.jersey().register(component.submissionResource());
        env.jersey().register(component.userStatsResource());

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            env.lifecycle().manage(component.trainingGradingResponsePoller());
        }

        env.admin().addTask(component.deleteTrainingProblemTask());
        env.admin().addTask(component.moveTrainingProblemToChapterTask());
        env.admin().addTask(component.moveTrainingProblemToProblemSetTask());
        env.admin().addTask(component.refreshContestStatsTask());
        env.admin().addTask(component.refreshProblemSetStatsTask());
        env.admin().addTask(component.replaceContestProblemTask());
    }
}
