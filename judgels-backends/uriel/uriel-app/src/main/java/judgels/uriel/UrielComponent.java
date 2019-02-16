package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.fs.aws.AwsModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.JudgelsScheduler;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.uriel.admin.AdminResource;
import judgels.uriel.contest.ContestResource;
import judgels.uriel.contest.announcement.ContestAnnouncementResource;
import judgels.uriel.contest.clarification.ContestClarificationResource;
import judgels.uriel.contest.contestant.ContestContestantResource;
import judgels.uriel.contest.file.ContestFileResource;
import judgels.uriel.contest.manager.ContestManagerResource;
import judgels.uriel.contest.module.ContestModuleResource;
import judgels.uriel.contest.problem.ContestProblemResource;
import judgels.uriel.contest.scoreboard.ContestScoreboardResource;
import judgels.uriel.contest.scoreboard.updater.ContestScoreboardUpdaterDispatcher;
import judgels.uriel.contest.scoreboard.updater.ContestScoreboardUpdaterModule;
import judgels.uriel.contest.submission.ContestSubmissionResource;
import judgels.uriel.contest.submission.bundle.ContestBundleItemSubmissionResource;
import judgels.uriel.contest.supervisor.ContestSupervisorResource;
import judgels.uriel.contest.web.ContestWebResource;
import judgels.uriel.file.FileModule;
import judgels.uriel.gabriel.GabrielModule;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.jophiel.JophielModule;
import judgels.uriel.rating.ContestRatingResource;
import judgels.uriel.sandalphon.SandalphonModule;
import judgels.uriel.sealtiel.SealtielModule;
import judgels.uriel.submission.SubmissionModule;

@Component(modules = {
        AwsModule.class,
        ContestScoreboardUpdaterModule.class,
        FileModule.class,
        GabrielModule.class,
        JophielModule.class,
        JudgelsModule.class,
        JudgelsApplicationModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        SandalphonModule.class,
        SealtielModule.class,
        SubmissionModule.class,
        UrielModule.class,
        UrielHibernateDaoModule.class})
@Singleton
public interface UrielComponent {
    AdminResource adminResource();
    ContestAnnouncementResource contestAnnouncementResource();
    ContestClarificationResource contestClarificationResource();
    ContestContestantResource contestContestantResource();
    ContestFileResource contestFileResource();
    ContestManagerResource contestManagerResource();
    ContestModuleResource contestModuleResource();
    ContestProblemResource contestProblemResource();
    ContestRatingResource contestRatingResource();
    ContestResource contestResource();
    ContestScoreboardResource contestScoreboardResource();
    ContestSubmissionResource contestSubmissionResource();
    ContestBundleItemSubmissionResource contestBundleSubmissionResource();
    ContestSupervisorResource contestSupervisorResource();
    ContestWebResource contestWebResource();
    VersionResource versionResource();

    JudgelsScheduler scheduler();
    ContestScoreboardUpdaterDispatcher contestScoreboardUpdaterDispatcher();
}
