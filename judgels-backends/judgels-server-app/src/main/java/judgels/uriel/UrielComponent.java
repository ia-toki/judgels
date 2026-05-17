package judgels.uriel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.admin.contest.ContestAdminResource;
import judgels.admin.contrib.contest.rating.ContestRatingAdminResource;
import judgels.admin.user.rating.UserRatingAdminResource;
import judgels.contest.ContestResource;
import judgels.contest.announcement.ContestAnnouncementResource;
import judgels.contest.clarification.ContestClarificationResource;
import judgels.contest.contestant.ContestContestantResource;
import judgels.contest.editorial.ContestEditorialResource;
import judgels.contest.file.ContestFileResource;
import judgels.contest.history.ContestHistoryResource;
import judgels.contest.log.ContestLogModule;
import judgels.contest.log.ContestLogPoller;
import judgels.contest.log.ContestLogResource;
import judgels.contest.manager.ContestManagerResource;
import judgels.contest.module.ContestModuleResource;
import judgels.contest.problem.ContestProblemResource;
import judgels.contest.scoreboard.ContestScoreboardPoller;
import judgels.contest.scoreboard.ContestScoreboardResource;
import judgels.contest.scoreboard.ContestScoreboardUpdaterModule;
import judgels.contest.submission.bundle.ContestItemSubmissionResource;
import judgels.contest.submission.programming.ContestSubmissionResource;
import judgels.contest.supervisor.ContestSupervisorResource;
import judgels.contest.web.ContestWebResource;
import judgels.contrib.contest.rating.ContestRatingModule;
import judgels.file.FileModule;
import judgels.grading.GradingClientModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.sandalphon.SandalphonClientModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.hibernate.JudgelsServerHibernateDaoModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.submission.bundle.ContestItemSubmissionModule;
import judgels.submission.programming.ContestSubmissionModule;
import judgels.submission.programming.GradingResponsePoller;
import judgels.tasks.DumpContestTask;
import judgels.tasks.UrielTaskModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsServerModule.class,
        JudgelsPersistenceModule.class,
        JudgelsSchedulerModule.class,

        // Database
        JudgelsHibernateModule.class,
        JudgelsServerHibernateDaoModule.class,

        // 3rd parties
        RabbitMQModule.class,
        SandalphonClientModule.class,
        GradingClientModule.class,

        // Features
        FileModule.class,
        ContestSubmissionModule.class,
        ContestItemSubmissionModule.class,
        ContestLogModule.class,
        ContestScoreboardUpdaterModule.class,
        ContestRatingModule.class,
        UrielTaskModule.class,

        tlx.uriel.tasks.TlxUrielTaskModule.class})
@Singleton
public interface UrielComponent {
    ContestAnnouncementResource contestAnnouncementResource();
    ContestClarificationResource contestClarificationResource();
    ContestContestantResource contestContestantResource();
    ContestEditorialResource contestEditorialResource();
    ContestFileResource contestFileResource();
    ContestHistoryResource contestHistoryResource();
    ContestItemSubmissionResource contestBundleSubmissionResource();
    ContestLogResource contestLogResource();
    ContestManagerResource contestManagerResource();
    ContestModuleResource contestModuleResource();
    ContestProblemResource contestProblemResource();
    ContestResource contestResource();
    ContestAdminResource contestAdminResource();
    ContestScoreboardResource contestScoreboardResource();
    ContestSubmissionResource contestProgrammingSubmissionResource();
    ContestSupervisorResource contestSupervisorResource();
    ContestWebResource contestWebResource();

    JudgelsScheduler scheduler();
    ContestLogPoller contestLogPoller();
    ContestScoreboardPoller contestScoreboardPoller();
    GradingResponsePoller gradingResponsePoller();

    DumpContestTask dumpContestTask();

    judgels.contrib.contest.rating.ContestRatingResource contestRatingResource();
    ContestRatingAdminResource contestRatingAdminResource();
    UserRatingAdminResource userRatingAdminResource();

    tlx.uriel.tasks.ReplaceProblemTask tlxReplaceProblemTask();
}
