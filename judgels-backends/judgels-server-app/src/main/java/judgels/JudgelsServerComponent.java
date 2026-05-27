package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestGradingResponsePoller;
import judgels.service.JudgelsScheduler;
import judgels.submission.programming.GradingResponsePoller;
import judgels.training.submission.programming.TrainingGradingResponsePoller;

@Component(modules = {
        // Judgels service
        judgels.service.JudgelsModule.class,
        judgels.JudgelsServerModule.class,
        judgels.service.persistence.JudgelsPersistenceModule.class,
        judgels.service.JudgelsSchedulerModule.class,

        // Database
        judgels.persistence.hibernate.JudgelsHibernateModule.class,
        judgels.persistence.hibernate.JudgelsServerHibernateDaoModule.class,

        // 3rd parties
        judgels.messaging.rabbitmq.RabbitMQModule.class,
        judgels.resource.ResourceModule.class,
        judgels.grading.GradingModule.class,
        judgels.mailer.MailerModule.class,

        // User features
        judgels.user.superadmin.SuperadminModule.class,
        judgels.user.avatar.UserAvatarModule.class,
        judgels.user.account.UserResetPasswordModule.class,
        judgels.session.SessionModule.class,
        judgels.user.web.WebModule.class,
        judgels.contrib.auth.AuthModule.class,
        judgels.contrib.recaptcha.RecaptchaModule.class,
        judgels.contrib.user.registration.UserRegistrationModule.class,

        // Problem features
        judgels.submission.SubmissionModule.class,

        // Contest features
        judgels.file.FileModule.class,
        judgels.contest.submission.programming.ContestSubmissionModule.class,
        judgels.contest.submission.bundle.ContestItemSubmissionModule.class,
        judgels.contest.log.ContestLogModule.class,
        judgels.contest.scoreboard.ContestScoreboardUpdaterModule.class,
        judgels.contrib.contest.rating.ContestRatingModule.class,

        // Training features
        judgels.training.submission.programming.TrainingSubmissionModule.class,
        judgels.training.submission.bundle.TrainingItemSubmissionModule.class,

        // Tasks
        judgels.tasks.JudgelsServerTaskModule.class,
        tlx.tasks.TlxTaskModule.class})
@Singleton
public interface JudgelsServerComponent {
    // Users
    judgels.profile.ProfileResource profileResource();
    judgels.session.SessionResource sessionResource();
    judgels.user.superadmin.SuperadminCreator superadminCreator();
    judgels.user.UserResource userResource();
    judgels.admin.user.UserAdminResource userAdminResource();
    judgels.user.account.UserAccountResource userAccountResource();
    judgels.user.avatar.UserAvatarResource userAvatarResource();
    judgels.user.info.UserInfoResource userProfileResource();
    judgels.admin.user.info.UserInfoAdminResource userInfoAdminResource();
    judgels.user.rating.UserRatingResource userRatingResource();
    judgels.admin.user.role.UserRoleAdminResource userRoleAdminResource();
    judgels.user.search.UserSearchResource userSearchResource();
    judgels.user.web.UserWebResource userWebResource();
    judgels.session.SessionCleaner sessionCleaner();
    judgels.contrib.session.SessionWithGoogleResource sessionWithGoogleResource();
    judgels.contrib.user.account.UserAccountWithRegistrationResource userAccountWithRegistrationResource();
    judgels.contrib.user.registration.web.UserRegistrationWebResource userRegistrationWebResource();

    // Problems
    judgels.problem.base.ProblemResource baseProblemResource();
    judgels.lesson.LessonResource lessonResource();
    @ContestGradingResponsePoller GradingResponsePoller contestGradingResponsePoller();
    GradingResponsePoller problemGradingResponsePoller();

    // Contests
    judgels.contest.ContestResource contestResource();
    judgels.admin.contest.ContestAdminResource contestAdminResource();
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
    judgels.contest.submission.programming.ContestSubmissionResource contestProgrammingSubmissionResource();
    judgels.contest.submission.bundle.ContestItemSubmissionResource contestBundleSubmissionResource();
    judgels.contest.supervisor.ContestSupervisorResource contestSupervisorResource();
    judgels.contest.log.ContestLogPoller contestLogPoller();
    judgels.contest.scoreboard.ContestScoreboardPoller contestScoreboardPoller();
    judgels.tasks.DumpContestTask dumpContestTask();
    judgels.contrib.contest.rating.ContestRatingResource contestRatingResource();
    judgels.admin.contrib.contest.rating.ContestRatingAdminResource contestRatingAdminResource();
    judgels.admin.user.rating.UserRatingAdminResource userRatingAdminResource();
    tlx.tasks.ReplaceProblemTask tlxReplaceProblemTask();

    // Training
    judgels.archive.ArchiveResource archiveResource();
    judgels.admin.archive.ArchiveAdminResource archiveAdminResource();
    judgels.curriculum.CurriculumResource curriculumResource();
    judgels.course.CourseResource courseResource();
    judgels.admin.course.CourseAdminResource courseAdminResource();
    judgels.chapter.ChapterResource chapterResource();
    judgels.admin.chapter.ChapterAdminResource chapterAdminResource();
    judgels.course.chapter.CourseChapterResource courseChapterResource();
    judgels.admin.course.chapter.CourseChapterAdminResource courseChapterAdminResource();
    judgels.chapter.lesson.ChapterLessonResource chapterLessonResource();
    judgels.admin.chapter.lesson.ChapterLessonAdminResource chapterLessonAdminResource();
    judgels.chapter.problem.ChapterProblemResource chapterProblemResource();
    judgels.admin.chapter.problem.ChapterProblemAdminResource chapterProblemAdminResource();
    judgels.problem.ProblemResource problemResource();
    judgels.problem.ProblemTagResource problemTagResource();
    judgels.problemset.ProblemSetResource problemSetResource();
    judgels.admin.problemset.ProblemSetAdminResource problemSetAdminResource();
    judgels.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
    judgels.admin.problemset.problem.ProblemSetProblemAdminResource problemSetProblemAdminResource();
    judgels.submission.bundle.ItemSubmissionResource itemSubmissionResource();
    judgels.submission.programming.SubmissionResource submissionResource();
    judgels.stats.UserStatsResource userStatsResource();
    @TrainingGradingResponsePoller GradingResponsePoller trainingGradingResponsePoller();
    judgels.tasks.RefreshContestStatsTask refreshContestStatsTask();
    judgels.tasks.RefreshProblemSetStatsTask refreshProblemSetStatsTask();
    tlx.tasks.DeleteProblemTask tlxDeleteProblemTask();
    tlx.tasks.MoveProblemToChapterTask tlxMoveProblemToChapterTask();
    tlx.tasks.MoveProblemToProblemSetTask tlxMoveProblemToProblemSetTask();

    JudgelsScheduler scheduler();
}
