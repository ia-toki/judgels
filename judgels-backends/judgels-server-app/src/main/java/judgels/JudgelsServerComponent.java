package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestGradingResponsePoller;
import judgels.service.JudgelsScheduler;
import judgels.submission.programming.GradingResponsePoller;

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
        tlx.auth.AuthModule.class,

        // Problem features
        judgels.submission.SubmissionModule.class,

        // Contest features
        judgels.file.FileModule.class,
        judgels.contest.submission.programming.ContestSubmissionModule.class,
        judgels.contest.submission.bundle.ContestItemSubmissionModule.class,
        judgels.contest.log.ContestLogModule.class,
        judgels.contest.scoreboard.ContestScoreboardUpdaterModule.class,
        judgels.contest.rating.ContestRatingModule.class,

        // Tasks
        judgels.tasks.JudgelsServerTaskModule.class})
@Singleton
public interface JudgelsServerComponent {
    // Users
    judgels.profile.ProfileResource profileResource();
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
    judgels.session.SessionCleaner sessionCleaner();

    // Problems
    judgels.problem.base.ProblemResource baseProblemResource();
    judgels.lesson.LessonResource lessonResource();
    @ContestGradingResponsePoller GradingResponsePoller contestGradingResponsePoller();
    GradingResponsePoller problemGradingResponsePoller();

    // Contests
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
    judgels.contest.submission.programming.ContestSubmissionResource contestProgrammingSubmissionResource();
    judgels.contest.submission.bundle.ContestItemSubmissionResource contestBundleSubmissionResource();
    judgels.contest.supervisor.ContestSupervisorResource contestSupervisorResource();
    judgels.contest.log.ContestLogPoller contestLogPoller();
    judgels.contest.scoreboard.ContestScoreboardPoller contestScoreboardPoller();
    judgels.tasks.DumpContestTask dumpContestTask();

    JudgelsScheduler scheduler();

    tlx.TlxServerComponent.Factory tlxServerComponentFactory();
}
