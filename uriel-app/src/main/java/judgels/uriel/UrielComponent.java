package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.fs.aws.AwsModule;
import judgels.uriel.contest.ContestResource;
import judgels.uriel.contest.announcement.ContestAnnouncementResource;
import judgels.uriel.contest.contestant.ContestContestantResource;
import judgels.uriel.contest.scoreboard.ContestScoreboardResource;
import judgels.uriel.contest.submission.ContestSubmissionResource;
import judgels.uriel.contest.web.ContestWebResource;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.hibernate.UrielHibernateModule;
import judgels.uriel.jophiel.JophielModule;
import judgels.uriel.sandalphon.SandalphonModule;
import judgels.uriel.submission.SubmissionModule;

@Component(modules = {
        JophielModule.class,
        SandalphonModule.class,
        UrielModule.class,
        UrielHibernateDaoModule.class,
        UrielHibernateModule.class,
        UrielPersistenceModule.class,
        AwsModule.class,
        SubmissionModule.class})
@Singleton
public interface UrielComponent {
    ContestResource contestResource();
    ContestWebResource contestWebResource();
    ContestAnnouncementResource contestAnnouncementResource();
    ContestScoreboardResource contestScoreboardResource();
    ContestContestantResource contestContestantResource();
    ContestSubmissionResource contestSubmissionResource();
    VersionResource versionResource();
}
