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
        judgels.service.persistence.hibernate.JudgelsHibernateModule.class,
        judgels.service.persistence.hibernate.JudgelsServerHibernateDaoModule.class,

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
    tlx.archive.ArchiveResource archiveResource();
    tlx.admin.archive.ArchiveAdminResource archiveAdminResource();
    tlx.curriculum.CurriculumResource curriculumResource();
    tlx.course.CourseResource courseResource();
    tlx.admin.course.CourseAdminResource courseAdminResource();
    tlx.chapter.ChapterResource chapterResource();
    tlx.admin.chapter.ChapterAdminResource chapterAdminResource();
    tlx.course.chapter.CourseChapterResource courseChapterResource();
    tlx.admin.course.chapter.CourseChapterAdminResource courseChapterAdminResource();
    tlx.chapter.lesson.ChapterLessonResource chapterLessonResource();
    tlx.admin.chapter.lesson.ChapterLessonAdminResource chapterLessonAdminResource();
    tlx.chapter.problem.ChapterProblemResource chapterProblemResource();
    tlx.admin.chapter.problem.ChapterProblemAdminResource chapterProblemAdminResource();
    tlx.problem.ProblemResource problemResource();
    tlx.problem.ProblemTagResource problemTagResource();
    tlx.problemset.ProblemSetResource problemSetResource();
    tlx.admin.problemset.ProblemSetAdminResource problemSetAdminResource();
    tlx.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
    tlx.admin.problemset.problem.ProblemSetProblemAdminResource problemSetProblemAdminResource();
    tlx.submission.bundle.ItemSubmissionResource itemSubmissionResource();
    tlx.submission.programming.SubmissionResource submissionResource();
    tlx.stats.UserStatsResource userStatsResource();
    @TrainingGradingResponsePoller GradingResponsePoller trainingGradingResponsePoller();
    judgels.tasks.RefreshContestStatsTask refreshContestStatsTask();
    judgels.tasks.RefreshProblemSetStatsTask refreshProblemSetStatsTask();
    tlx.tasks.DeleteProblemTask tlxDeleteProblemTask();
    tlx.tasks.MoveProblemToChapterTask tlxMoveProblemToChapterTask();
    tlx.tasks.MoveProblemToProblemSetTask tlxMoveProblemToProblemSetTask();

    JudgelsScheduler scheduler();
}
