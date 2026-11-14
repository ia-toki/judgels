package judgels;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import java.time.Duration;
import judgels.contest.rating.ContestRatingModule;
import judgels.core.JudgelsJerseyFeature;
import judgels.core.JudgelsObjectMappers;
import judgels.core.JudgelsSchedulerModule;
import judgels.core.auth.AuthModule;
import judgels.core.mailer.MailerModule;
import judgels.core.messaging.RabbitMQModule;
import judgels.core.recaptcha.RecaptchaModule;
import judgels.grading.GradingModule;
import judgels.michael.DaggerMichaelComponent;
import judgels.michael.MichaelComponent;
import judgels.persistence.JudgelsHibernateModule;
import judgels.rating.RatingConfiguration;
import judgels.session.SessionModule;
import judgels.setting.SettingModule;
import judgels.training.TrainingConfiguration;
import judgels.training.submission.bundle.TrainingItemSubmissionModule;
import judgels.training.submission.programming.TrainingSubmissionModule;
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
        RabbitMQModule rabbitMQModule = new RabbitMQModule(config.getJudgelsConfig().getRabbitMQConfig());

        runMichael(config, env, rabbitMQModule);
        runJudgelsServer(config, env, rabbitMQModule);
    }

    private void runMichael(
            JudgelsServerApplicationConfiguration config,
            Environment env,
            RabbitMQModule rabbitMQModule) {

        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();

        MichaelComponent component = DaggerMichaelComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(judgelsConfig.getAuthConfig()))
                .rabbitMQModule(rabbitMQModule)
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

    private void runJudgelsServer(
            JudgelsServerApplicationConfiguration config,
            Environment env,
            RabbitMQModule rabbitMQModule) {

        JudgelsServerConfiguration judgelsConfig = config.getJudgelsConfig();

        boolean ratingEnabled = judgelsConfig.getRatingConfig()
                .map(RatingConfiguration::getEnabled)
                .orElse(false);
        boolean trainingEnabled = judgelsConfig.getTrainingConfig()
                .map(TrainingConfiguration::getEnabled)
                .orElse(false);

        JudgelsServerComponent component = DaggerJudgelsServerComponent.builder()
                .judgelsServerModule(new JudgelsServerModule(judgelsConfig))
                .judgelsSchedulerModule(new JudgelsSchedulerModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .authModule(new AuthModule(judgelsConfig.getAuthConfig()))
                .rabbitMQModule(rabbitMQModule)
                .mailerModule(new MailerModule(judgelsConfig.getMailerConfig()))
                .recaptchaModule(new RecaptchaModule(judgelsConfig.getRecaptchaConfig()))
                .settingModule(new SettingModule(
                        judgelsConfig.getUserRegistrationConfig(),
                        judgelsConfig.getUserResetPasswordConfig(),
                        judgelsConfig.getGoogleAnalyticsConfig()))
                .sessionModule(new SessionModule())
                .superadminModule(new SuperadminModule(judgelsConfig.getSuperadminConfig()))
                .gradingModule(new GradingModule(judgelsConfig.getGradingConfig()))
                .contestRatingModule(new ContestRatingModule(judgelsConfig.getRatingConfig()))
                .trainingSubmissionModule(new TrainingSubmissionModule(judgelsConfig.getTrainingConfig()))
                .trainingItemSubmissionModule(new TrainingItemSubmissionModule(judgelsConfig.getTrainingConfig()))
                .build();

        component.settingCreator().initializeSettings();
        component.superadminCreator().ensureSuperadminExists();

        env.jersey().register(component.settingResource());
        env.jersey().register(component.sessionResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.userAvatarResource());
        env.jersey().register(component.userInfoResource());
        env.jersey().register(component.userRoleResource());
        env.jersey().register(component.userWebResource());
        env.jersey().register(component.profileResource());

        env.jersey().register(component.problemResource());
        env.jersey().register(component.problemRenderResource());
        env.jersey().register(component.problemCloneRenderResource());
        env.jersey().register(component.problemItemResource());
        env.jersey().register(component.problemEditorialResource());
        env.jersey().register(component.problemGradingResource());
        env.jersey().register(component.problemPartnerResource());
        env.jersey().register(component.problemSetterResource());
        env.jersey().register(component.problemStatementResource());
        env.jersey().register(component.problemTagResource());
        env.jersey().register(component.problemVersionResource());
        env.jersey().register(component.problemWriterResource());
        env.jersey().register(component.problemBundleSubmissionResource());
        env.jersey().register(component.problemSubmissionResource());

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

        env.lifecycle().manage(component.problemGradingResponsePoller());
        env.lifecycle().manage(component.contestGradingResponsePoller());

        if (judgelsConfig.getGradingConfig().getFakeGraderEnabled()) {
            env.lifecycle().manage(component.fakeGradingRequestPoller());
        }

        env.admin().addTask(component.dumpContestTask());

        if (ratingEnabled) {
            env.jersey().register(component.userRatingResource());
            env.jersey().register(component.contestRatingResource());
        }

        if (trainingEnabled) {
            component.curriculumCreator().ensureCurriculumExists();

            env.jersey().register(component.accountRegistrationResource());
            env.jersey().register(component.accountPasswordResource());

            env.jersey().register(component.lessonResource());
            env.jersey().register(component.lessonRenderResource());
            env.jersey().register(component.lessonCloneRenderResource());
            env.jersey().register(component.lessonStatementResource());
            env.jersey().register(component.lessonVersionResource());

            env.jersey().register(component.archiveResource());
            env.jersey().register(component.curriculumResource());
            env.jersey().register(component.courseResource());
            env.jersey().register(component.chapterResource());
            env.jersey().register(component.courseChapterResource());
            env.jersey().register(component.chapterLessonResource());
            env.jersey().register(component.chapterProblemResource());
            env.jersey().register(component.problemSetResource());
            env.jersey().register(component.problemSetProblemResource());

            env.jersey().register(component.trainingProblemResource());
            env.jersey().register(component.trainingItemSubmissionResource());
            env.jersey().register(component.trainingSubmissionResource());
            env.jersey().register(component.trainingProblemTagResource());
            env.jersey().register(component.trainingUserStatsResource());

            env.jersey().register(component.legacyProblemResource());
            env.jersey().register(component.legacyProblemTagResource());
            env.jersey().register(component.legacyUserStatsResource());
            env.jersey().register(component.legacySubmissionResource());
            env.jersey().register(component.legacyItemSubmissionResource());

            env.lifecycle().manage(component.trainingGradingResponsePoller());

            env.admin().addTask(component.replaceContestProblemTask());
            env.admin().addTask(component.refreshContestStatsTask());
            env.admin().addTask(component.deleteTrainingProblemTask());
            env.admin().addTask(component.moveTrainingProblemToChapterTask());
            env.admin().addTask(component.moveTrainingProblemToProblemSetTask());
            env.admin().addTask(component.refreshProblemSetStatsTask());
        }
    }
}
