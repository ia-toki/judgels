package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestGradingResponsePoller;
import judgels.core.JudgelsScheduler;
import judgels.submission.programming.GradingResponsePoller;
import judgels.training.submission.programming.TrainingGradingResponsePoller;

@Component(modules = {
        judgels.JudgelsServerModule.class,

        judgels.core.JudgelsModule.class,
        judgels.core.JudgelsSchedulerModule.class,
        judgels.core.messaging.RabbitMQModule.class,
        judgels.core.auth.AuthModule.class,
        judgels.core.mailer.MailerModule.class,
        judgels.core.recaptcha.RecaptchaModule.class,

        judgels.persistence.JudgelsPersistenceModule.class,
        judgels.persistence.JudgelsHibernateModule.class,

        judgels.user.superadmin.SuperadminModule.class,
        judgels.user.avatar.UserAvatarModule.class,
        judgels.session.SessionModule.class,
        judgels.setting.SettingModule.class,

        judgels.grading.GradingModule.class,

        judgels.catalog.CatalogModule.class,

        judgels.submission.SubmissionModule.class,

        judgels.contest.file.ContestFileModule.class,
        judgels.contest.log.ContestLogModule.class,
        judgels.contest.rating.ContestRatingModule.class,
        judgels.contest.scoreboard.ContestScoreboardUpdaterModule.class,
        judgels.contest.submission.programming.ContestSubmissionModule.class,
        judgels.contest.submission.bundle.ContestItemSubmissionModule.class,

        judgels.training.curriculum.CurriculumModule.class,
        judgels.training.account.registration.AccountRegistrationModule.class,
        judgels.training.account.password.AccountPasswordResetModule.class,
        judgels.training.submission.programming.TrainingSubmissionModule.class,
        judgels.training.submission.bundle.TrainingItemSubmissionModule.class,

        judgels.tasks.JudgelsServerTaskModule.class})
@Singleton
public interface JudgelsServerComponent {
    JudgelsScheduler scheduler();

    judgels.session.SessionResource sessionResource();
    judgels.session.SessionCleaner sessionCleaner();
    judgels.user.superadmin.SuperadminCreator superadminCreator();
    judgels.user.UserResource userResource();
    judgels.user.avatar.UserAvatarResource userAvatarResource();
    judgels.user.info.UserInfoResource userInfoResource();
    judgels.user.rating.UserRatingResource userRatingResource();
    judgels.user.role.UserRoleResource userRoleResource();
    judgels.user.search.UserSearchResource userSearchResource();
    judgels.user.web.UserWebResource userWebResource();
    judgels.profile.ProfileResource profileResource();
    judgels.setting.SettingResource settingResource();
    judgels.setting.SettingCreator settingCreator();

    judgels.grading.FakeGradingRequestPoller fakeGradingRequestPoller();

    judgels.catalog.problem.ProblemResource problemResource();
    judgels.catalog.problem.ProblemRenderResource problemRenderResource();
    judgels.catalog.problem.ProblemCloneRenderResource problemCloneRenderResource();
    judgels.catalog.problem.bundle.item.ProblemItemResource problemItemResource();
    judgels.catalog.problem.editorial.ProblemEditorialResource problemEditorialResource();
    judgels.catalog.problem.programming.grading.ProblemGradingResource problemGradingResource();
    judgels.catalog.problem.partner.ProblemPartnerResource problemPartnerResource();
    judgels.catalog.problem.setter.ProblemSetterResource problemSetterResource();
    judgels.catalog.problem.statement.ProblemStatementResource problemStatementResource();
    judgels.catalog.problem.tag.ProblemTagResource problemTagResource();
    judgels.catalog.problem.version.ProblemVersionResource problemVersionResource();
    judgels.catalog.problem.writer.ProblemWriterResource problemWriterResource();
    judgels.catalog.lesson.LessonResource lessonResource();
    judgels.catalog.lesson.LessonRenderResource lessonRenderResource();
    judgels.catalog.lesson.LessonCloneRenderResource lessonCloneRenderResource();
    judgels.catalog.lesson.statement.LessonStatementResource lessonStatementResource();
    judgels.catalog.lesson.version.LessonVersionResource lessonVersionResource();

    judgels.submission.bundle.ProblemBundleSubmissionResource problemBundleSubmissionResource();
    judgels.submission.programming.ProblemSubmissionResource problemSubmissionResource();
    GradingResponsePoller problemGradingResponsePoller();

    judgels.contest.ContestResource contestResource();
    judgels.contest.web.ContestWebResource contestWebResource();
    judgels.contest.announcement.ContestAnnouncementResource contestAnnouncementResource();
    judgels.contest.clarification.ContestClarificationResource contestClarificationResource();
    judgels.contest.contestant.ContestContestantResource contestContestantResource();
    judgels.contest.editorial.ContestEditorialResource contestEditorialResource();
    judgels.contest.file.ContestFileResource contestFileResource();
    judgels.contest.history.ContestHistoryResource contestHistoryResource();
    judgels.contest.log.ContestLogResource contestLogResource();
    judgels.contest.manager.ContestManagerResource contestManagerResource();
    judgels.contest.module.ContestModuleResource contestModuleResource();
    judgels.contest.problem.ContestProblemResource contestProblemResource();
    judgels.contest.rating.ContestRatingResource contestRatingResource();
    judgels.contest.scoreboard.ContestScoreboardResource contestScoreboardResource();
    judgels.contest.submission.programming.ContestSubmissionResource contestSubmissionResource();
    judgels.contest.submission.bundle.ContestItemSubmissionResource contestItemSubmissionResource();
    judgels.contest.supervisor.ContestSupervisorResource contestSupervisorResource();
    judgels.contest.log.ContestLogPoller contestLogPoller();
    judgels.contest.scoreboard.ContestScoreboardPoller contestScoreboardPoller();
    @ContestGradingResponsePoller GradingResponsePoller contestGradingResponsePoller();

    judgels.training.curriculum.CurriculumCreator curriculumCreator();
    judgels.training.curriculum.CurriculumResource curriculumResource();
    judgels.training.account.registration.AccountRegistrationResource accountRegistrationResource();
    judgels.training.account.password.AccountPasswordResource accountPasswordResource();
    judgels.training.archive.ArchiveResource archiveResource();
    judgels.training.course.CourseResource courseResource();
    judgels.training.course.chapter.CourseChapterResource courseChapterResource();
    judgels.training.chapter.ChapterResource chapterResource();
    judgels.training.chapter.lesson.ChapterLessonResource chapterLessonResource();
    judgels.training.chapter.problem.ChapterProblemResource chapterProblemResource();
    judgels.training.problemset.ProblemSetResource problemSetResource();
    judgels.training.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
    judgels.training.problem.TrainingProblemResource trainingProblemResource();
    judgels.training.problem.TrainingProblemTagResource trainingProblemTagResource();
    judgels.training.stats.TrainingUserStatsResource trainingUserStatsResource();
    judgels.training.submission.programming.TrainingSubmissionResource trainingSubmissionResource();
    judgels.training.submission.bundle.TrainingItemSubmissionResource trainingItemSubmissionResource();
    judgels.training.LegacyTrainingResources.ProblemResource legacyProblemResource();
    judgels.training.LegacyTrainingResources.ProblemTagResource legacyProblemTagResource();
    judgels.training.LegacyTrainingResources.UserStatsResource legacyUserStatsResource();
    judgels.training.LegacyTrainingResources.SubmissionResource legacySubmissionResource();
    judgels.training.LegacyTrainingResources.ItemSubmissionResource legacyItemSubmissionResource();
    @TrainingGradingResponsePoller GradingResponsePoller trainingGradingResponsePoller();

    judgels.tasks.DumpContestTask dumpContestTask();
    judgels.tasks.ReplaceContestProblemTask replaceContestProblemTask();
    judgels.tasks.RefreshContestStatsTask refreshContestStatsTask();
    judgels.tasks.RefreshProblemSetStatsTask refreshProblemSetStatsTask();
    judgels.tasks.DeleteTrainingProblemTask deleteTrainingProblemTask();
    judgels.tasks.MoveTrainingProblemToChapterTask moveTrainingProblemToChapterTask();
    judgels.tasks.MoveTrainingProblemToProblemSetTask moveTrainingProblemToProblemSetTask();
}
