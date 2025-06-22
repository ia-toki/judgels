package judgels.uriel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.contrib.fs.aws.AwsModule;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.sandalphon.SandalphonClientModule;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.sandalphon.submission.programming.GradingResponsePoller;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.gabriel.GabrielClientModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.uriel.contest.ContestResource;
import judgels.uriel.contest.announcement.ContestAnnouncementResource;
import judgels.uriel.contest.clarification.ContestClarificationResource;
import judgels.uriel.contest.contestant.ContestContestantResource;
import judgels.uriel.contest.editorial.ContestEditorialResource;
import judgels.uriel.contest.file.ContestFileResource;
import judgels.uriel.contest.history.ContestHistoryResource;
import judgels.uriel.contest.log.ContestLogModule;
import judgels.uriel.contest.log.ContestLogPoller;
import judgels.uriel.contest.log.ContestLogResource;
import judgels.uriel.contest.manager.ContestManagerResource;
import judgels.uriel.contest.module.ContestModuleResource;
import judgels.uriel.contest.problem.ContestProblemResource;
import judgels.uriel.contest.scoreboard.ContestScoreboardPoller;
import judgels.uriel.contest.scoreboard.ContestScoreboardResource;
import judgels.uriel.contest.scoreboard.ContestScoreboardUpdaterModule;
import judgels.uriel.contest.submission.bundle.ContestItemSubmissionResource;
import judgels.uriel.contest.submission.programming.ContestSubmissionResource;
import judgels.uriel.contest.supervisor.ContestSupervisorResource;
import judgels.uriel.contest.web.ContestWebResource;
import judgels.uriel.file.FileModule;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.submission.bundle.ItemSubmissionModule;
import judgels.uriel.submission.programming.SubmissionModule;
import judgels.uriel.tasks.DumpContestTask;
import judgels.uriel.tasks.UrielTaskModule;
import tlx.uriel.tasks.TlxUrielTaskModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsServerModule.class,
        JudgelsPersistenceModule.class,
        JudgelsSchedulerModule.class,

        // Database
        JudgelsHibernateModule.class,
        JophielHibernateDaoModule.class,
        SandalphonHibernateDaoModule.class,
        UrielHibernateDaoModule.class,

        // 3rd parties
        RabbitMQModule.class,
        SandalphonClientModule.class,
        GabrielClientModule.class,

        // Features
        FileModule.class,
        SubmissionModule.class,
        ItemSubmissionModule.class,
        ContestLogModule.class,
        ContestScoreboardUpdaterModule.class,
        UrielTaskModule.class,

        // TLX
        AwsModule.class,
        TlxUrielTaskModule.class})
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
    ContestScoreboardResource contestScoreboardResource();
    ContestSubmissionResource contestProgrammingSubmissionResource();
    ContestSupervisorResource contestSupervisorResource();
    ContestWebResource contestWebResource();

    JudgelsScheduler scheduler();
    ContestLogPoller contestLogPoller();
    ContestScoreboardPoller contestScoreboardPoller();
    GradingResponsePoller gradingResponsePoller();

    DumpContestTask dumpContestTask();

    judgels.contrib.uriel.contest.rating.ContestRatingResource tlxContestRatingResource();
    tlx.uriel.tasks.ReplaceProblemTask tlxReplaceProblemTask();
}
