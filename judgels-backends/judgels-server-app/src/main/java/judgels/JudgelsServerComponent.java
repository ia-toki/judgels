package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestGradingResponsePoller;
import judgels.service.JudgelsScheduler;
import judgels.submission.programming.GradingResponsePoller;

@Component(modules = {
        judgels.JudgelsServerModule.class,
        judgels.service.JudgelsModule.class,
        judgels.service.JudgelsSchedulerModule.class,
        judgels.service.persistence.JudgelsPersistenceModule.class,
        judgels.service.persistence.hibernate.JudgelsHibernateModule.class,
        judgels.service.persistence.hibernate.JudgelsServerHibernateDaoModule.class,

        judgels.user.superadmin.SuperadminModule.class,
        judgels.user.avatar.UserAvatarModule.class,
        judgels.user.web.WebModule.class,
        judgels.session.SessionModule.class,

        judgels.messaging.rabbitmq.RabbitMQModule.class,
        judgels.grading.GradingModule.class,
        judgels.file.FileModule.class,

        judgels.resource.ResourceModule.class,
        judgels.submission.SubmissionModule.class,

        judgels.contest.submission.programming.ContestSubmissionModule.class,
        judgels.contest.submission.bundle.ContestItemSubmissionModule.class,
        judgels.contest.log.ContestLogModule.class,
        judgels.contest.scoreboard.ContestScoreboardUpdaterModule.class,
        judgels.contest.rating.ContestRatingModule.class,

        judgels.tasks.JudgelsServerTaskModule.class,

        tlx.auth.AuthModule.class})
@Singleton
public interface JudgelsServerComponent {
    judgels.profile.ProfileResource profileResource();
    judgels.session.SessionResource sessionResource();
    judgels.user.superadmin.SuperadminCreator superadminCreator();
    judgels.user.UserResource userResource();
    judgels.user.avatar.UserAvatarResource userAvatarResource();
    judgels.user.info.UserInfoResource userProfileResource();
    judgels.user.rating.UserRatingResource userRatingResource();
    judgels.user.role.UserRoleResource userRoleResource();
    judgels.user.search.UserSearchResource userSearchResource();
    judgels.user.web.UserWebResource userWebResource();
    judgels.session.SessionCleaner sessionCleaner();

    judgels.problem.base.ProblemResource problemResource();
    judgels.problem.ProblemTagResource problemTagResource();
    judgels.lesson.LessonResource lessonResource();
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
    judgels.contest.scoreboard.ContestScoreboardResource contestScoreboardResource();
    judgels.contest.submission.programming.ContestSubmissionResource contestProgrammingSubmissionResource();
    judgels.contest.submission.bundle.ContestItemSubmissionResource contestBundleSubmissionResource();
    judgels.contest.supervisor.ContestSupervisorResource contestSupervisorResource();
    judgels.contest.log.ContestLogPoller contestLogPoller();
    judgels.contest.scoreboard.ContestScoreboardPoller contestScoreboardPoller();
    @ContestGradingResponsePoller GradingResponsePoller contestGradingResponsePoller();

    judgels.tasks.DumpContestTask dumpContestTask();

    JudgelsScheduler scheduler();

    tlx.TlxServerComponent.Factory tlxServerComponentFactory();
}
