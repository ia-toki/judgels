package judgels;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import java.time.Duration;
import java.util.Optional;
import judgels.core.JudgelsApp;
import judgels.core.JudgelsJerseyFeature;
import judgels.core.JudgelsObjectMappers;
import judgels.core.JudgelsSchedulerModule;
import judgels.core.auth.AuthModule;
import judgels.core.fs.FileSystem;
import judgels.core.fs.aws.AwsConfiguration;
import judgels.core.fs.aws.AwsFileSystem;
import judgels.core.fs.aws.AwsFsConfiguration;
import judgels.core.mailer.MailerModule;
import judgels.core.messaging.RabbitMQModule;
import judgels.core.recaptcha.RecaptchaModule;
import judgels.grading.GradingModule;
import judgels.michael.DaggerMichaelComponent;
import judgels.michael.MichaelComponent;
import judgels.persistence.JudgelsHibernateModule;
import judgels.session.SessionModule;
import judgels.training.TrainingConfiguration;
import judgels.training.stats.StatsConfiguration;
import judgels.training.submission.bundle.TrainingItemSubmissionModule;
import judgels.training.submission.programming.TrainingSubmissionModule;
import judgels.user.account.UserResetPasswordModule;
import judgels.user.registration.UserRegistrationModule;
import judgels.user.registration.web.UserRegistrationWebConfig;
import judgels.user.superadmin.SuperadminModule;
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
        runJudgelsServer(config, env);
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

    private void runJudgelsServer(JudgelsServerApplicationConfiguration config, Environment env) {
        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();

        StatsConfiguration statsConfig = StatsConfiguration.DEFAULT;
        Optional<FileSystem> trainingSubmissionFs = Optional.empty();
        if (JudgelsApp.isTLX() && config.getTrainingConfig().isPresent()) {
            TrainingConfiguration trainingConfig = config.getTrainingConfig().get();
            statsConfig = trainingConfig.getStatsConfig();

            if (trainingConfig.getSubmissionConfig().isPresent()
                    && trainingConfig.getAwsConfig().isPresent()
                    && trainingConfig.getSubmissionConfig().get().getFs() instanceof AwsFsConfiguration) {
                AwsConfiguration awsConfig = trainingConfig.getAwsConfig().get();
                AwsFsConfiguration submissionFsConfig = (AwsFsConfiguration) trainingConfig.getSubmissionConfig().get().getFs();
                trainingSubmissionFs = Optional.of(new AwsFileSystem(awsConfig, submissionFsConfig));
            }
        }

        JudgelsServerComponent component = DaggerJudgelsServerComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(judgelsConfig.getAuthConfig()))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .gradingModule(new GradingModule(judgelsConfig.getGradingConfig()))
                .superadminModule(new SuperadminModule(judgelsConfig.getSuperadminCreatorConfig()))
                .sessionModule(new SessionModule())
                .mailerModule(new MailerModule(judgelsConfig.getMailerConfig()))
                .recaptchaModule(new RecaptchaModule(judgelsConfig.getRecaptchaConfig()))
                .userRegistrationModule(new UserRegistrationModule(UserRegistrationWebConfig.fromServerConfig(judgelsConfig)))
                .userResetPasswordModule(new UserResetPasswordModule(judgelsConfig.getUserResetPasswordConfig()))
                .trainingSubmissionModule(new TrainingSubmissionModule(statsConfig, trainingSubmissionFs))
                .trainingItemSubmissionModule(new TrainingItemSubmissionModule(statsConfig))
                .build();

        component.superadminCreator().ensureSuperadminExists();
        component.settingCreator().initializeSettings();

        if (JudgelsApp.isTLX()) {
            component.curriculumCreator().ensureCurriculumExists();
        }

        // Users
        env.jersey().register(component.sessionResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.userAvatarResource());
        env.jersey().register(component.userProfileResource());
        env.jersey().register(component.userRatingResource());
        env.jersey().register(component.userRoleResource());
        env.jersey().register(component.userSearchResource());
        env.jersey().register(component.userWebResource());
        env.jersey().register(component.profileResource());

        if (JudgelsApp.isTLX()) {
            env.jersey().register(component.userAccountResource());
            env.jersey().register(component.userRegistrationWebResource());
        }

        component.scheduler().scheduleWithFixedDelay(
                "session-cleaner",
                component.sessionCleaner(),
                Duration.ofDays(1));

        // Problems
        env.jersey().register(component.problemResource());
        env.jersey().register(component.problemRenderResource());
        env.jersey().register(component.lessonResource());
        env.jersey().register(component.lessonRenderResource());

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            env.lifecycle().manage(component.problemGradingResponsePoller());
        }

        // Contests
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

        if (JudgelsApp.isTLX()) {
            env.jersey().register(component.contestRatingResource());
        }

        component.scheduler().scheduleWithFixedDelay(
                "contest-scoreboard-poller",
                component.contestScoreboardPoller(),
                Duration.ofSeconds(10));

        component.scheduler().scheduleWithFixedDelay(
                "contest-log-poller",
                component.contestLogPoller(),
                Duration.ofSeconds(3));

        if (judgelsConfig.getRabbitMQConfig().isPresent()) {
            env.lifecycle().manage(component.contestGradingResponsePoller());
        }

        env.admin().addTask(component.dumpContestTask());

        if (JudgelsApp.isTLX()) {
            env.admin().addTask(component.replaceContestProblemTask());
        }

        // Training
        if (JudgelsApp.isTLX()) {
            env.jersey().register(component.archiveResource());
            env.jersey().register(component.curriculumResource());
            env.jersey().register(component.courseResource());
            env.jersey().register(component.chapterResource());
            env.jersey().register(component.courseChapterResource());
            env.jersey().register(component.chapterLessonResource());
            env.jersey().register(component.chapterProblemResource());
            env.jersey().register(component.trainingProblemResource());
            env.jersey().register(component.problemSetResource());
            env.jersey().register(component.problemSetProblemResource());
            env.jersey().register(component.trainingItemSubmissionResource());
            env.jersey().register(component.trainingSubmissionResource());
            env.jersey().register(component.trainingProblemTagResource());
            env.jersey().register(component.trainingUserStatsResource());

            if (judgelsConfig.getRabbitMQConfig().isPresent()) {
                env.lifecycle().manage(component.trainingGradingResponsePoller());
            }

            env.admin().addTask(component.deleteTrainingProblemTask());
            env.admin().addTask(component.moveTrainingProblemToChapterTask());
            env.admin().addTask(component.moveTrainingProblemToProblemSetTask());
            env.admin().addTask(component.refreshContestStatsTask());
            env.admin().addTask(component.refreshProblemSetStatsTask());
        }

        // Settings
        env.jersey().register(component.settingResource());
    }
}
