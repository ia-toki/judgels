package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestGradingResponsePoller;
import judgels.service.JudgelsScheduler;
import judgels.submission.programming.GradingResponsePoller;
import judgels.training.submission.programming.TrainingGradingResponsePoller;

@Component(modules = {
        judgels.JudgelsServerModule.class,
        judgels.service.JudgelsModule.class,
        judgels.service.JudgelsSchedulerModule.class,
        judgels.persistence.JudgelsPersistenceModule.class,
        judgels.persistence.hibernate.JudgelsHibernateModule.class,
        judgels.persistence.hibernate.JudgelsServerHibernateDaoModule.class,

        judgels.user.superadmin.SuperadminModule.class,
        judgels.user.avatar.UserAvatarModule.class,
        judgels.session.SessionModule.class,
        judgels.setting.SettingModule.class,

        judgels.messaging.rabbitmq.RabbitMQModule.class,
        judgels.grading.GradingModule.class,
        judgels.file.FileModule.class,

        judgels.catalog.CatalogModule.class,
        judgels.submission.SubmissionModule.class,

        judgels.contest.submission.programming.ContestSubmissionModule.class,
        judgels.contest.submission.bundle.ContestItemSubmissionModule.class,
        judgels.contest.log.ContestLogModule.class,
        judgels.contest.scoreboard.ContestScoreboardUpdaterModule.class,
        judgels.contest.rating.ContestRatingModule.class,

        judgels.training.submission.programming.TrainingSubmissionModule.class,
        judgels.training.submission.bundle.TrainingItemSubmissionModule.class,
        judgels.training.curriculum.CurriculumModule.class,

        judgels.auth.AuthModule.class,
        judgels.mailer.MailerModule.class,
        judgels.recaptcha.RecaptchaModule.class,
        judgels.user.registration.UserRegistrationModule.class,
        judgels.user.account.UserResetPasswordModule.class,

        judgels.tasks.JudgelsServerTaskModule.class})
@Singleton
public interface JudgelsServerComponent {
    judgels.session.SessionResource sessionResource();
    judgels.user.superadmin.SuperadminCreator superadminCreator();
    judgels.user.UserResource userResource();
    judgels.user.account.UserAccountResource userAccountResource();
    judgels.user.avatar.UserAvatarResource userAvatarResource();
    judgels.user.info.UserInfoResource userProfileResource();
    judgels.user.rating.UserRatingResource userRatingResource();
    judgels.user.role.UserRoleResource userRoleResource();
    judgels.user.search.UserSearchResource userSearchResource();
    judgels.user.web.UserWebResource userWebResource();
    judgels.user.registration.web.UserRegistrationWebResource userRegistrationWebResource();
    judgels.profile.ProfileResource profileResource();

    judgels.problem.ProblemResource problemResource();
    judgels.problem.ProblemRenderResource problemRenderResource();
    judgels.lesson.LessonResource lessonResource();
    judgels.lesson.LessonRenderResource lessonRenderResource();

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
    judgels.contest.scoreboard.ContestScoreboardResource contestScoreboardResource();
    judgels.contest.submission.programming.ContestSubmissionResource contestSubmissionResource();
    judgels.contest.submission.bundle.ContestItemSubmissionResource contestItemSubmissionResource();
    judgels.contest.supervisor.ContestSupervisorResource contestSupervisorResource();
    judgels.contest.log.ContestLogPoller contestLogPoller();
    judgels.contest.scoreboard.ContestScoreboardPoller contestScoreboardPoller();
    judgels.contest.rating.ContestRatingResource contestRatingResource();

    judgels.training.curriculum.CurriculumCreator curriculumCreator();
    judgels.training.curriculum.CurriculumResource curriculumResource();
    judgels.training.archive.ArchiveResource archiveResource();
    judgels.training.course.CourseResource courseResource();
    judgels.training.chapter.ChapterResource chapterResource();
    judgels.training.course.chapter.CourseChapterResource courseChapterResource();
    judgels.training.chapter.lesson.ChapterLessonResource chapterLessonResource();
    judgels.training.chapter.problem.ChapterProblemResource chapterProblemResource();
    judgels.training.problem.TrainingProblemResource trainingProblemResource();
    judgels.training.problemset.ProblemSetResource problemSetResource();
    judgels.training.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
    judgels.training.submission.bundle.TrainingItemSubmissionResource trainingItemSubmissionResource();
    judgels.training.submission.programming.TrainingSubmissionResource trainingSubmissionResource();
    judgels.training.problem.TrainingProblemTagResource trainingProblemTagResource();
    judgels.training.stats.TrainingUserStatsResource trainingUserStatsResource();

    judgels.setting.SettingResource settingResource();
    judgels.setting.SettingCreator settingCreator();

    judgels.session.SessionCleaner sessionCleaner();
    GradingResponsePoller problemGradingResponsePoller();
    @ContestGradingResponsePoller GradingResponsePoller contestGradingResponsePoller();
    @TrainingGradingResponsePoller GradingResponsePoller trainingGradingResponsePoller();

    judgels.tasks.DumpContestTask dumpContestTask();
    judgels.tasks.DeleteTrainingProblemTask deleteTrainingProblemTask();
    judgels.tasks.MoveTrainingProblemToChapterTask moveTrainingProblemToChapterTask();
    judgels.tasks.MoveTrainingProblemToProblemSetTask moveTrainingProblemToProblemSetTask();
    judgels.tasks.RefreshContestStatsTask refreshContestStatsTask();
    judgels.tasks.RefreshProblemSetStatsTask refreshProblemSetStatsTask();
    judgels.tasks.ReplaceContestProblemTask replaceContestProblemTask();

    JudgelsScheduler scheduler();
}
