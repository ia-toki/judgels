package judgels.jerahmeel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.fs.aws.AwsModule;
import judgels.jerahmeel.archive.ArchiveResource;
import judgels.jerahmeel.chapter.ChapterResource;
import judgels.jerahmeel.chapter.lesson.ChapterLessonResource;
import judgels.jerahmeel.chapter.problem.ChapterProblemResource;
import judgels.jerahmeel.course.CourseResource;
import judgels.jerahmeel.course.chapter.CourseChapterResource;
import judgels.jerahmeel.curriculum.CurriculumResource;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jerahmeel.problem.ProblemResource;
import judgels.jerahmeel.problem.ProblemTagResource;
import judgels.jerahmeel.problemset.ProblemSetResource;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemResource;
import judgels.jerahmeel.stats.UserStatsResource;
import judgels.jerahmeel.submission.bundle.ItemSubmissionModule;
import judgels.jerahmeel.submission.bundle.ItemSubmissionResource;
import judgels.jerahmeel.submission.programming.ContestStatsTask;
import judgels.jerahmeel.submission.programming.ProblemSetStatsTask;
import judgels.jerahmeel.submission.programming.StatsModule;
import judgels.jerahmeel.submission.programming.SubmissionModule;
import judgels.jerahmeel.submission.programming.SubmissionResource;
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
import judgels.uriel.hibernate.UrielHibernateDaoModule;

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
        JerahmeelHibernateDaoModule.class,

        // 3rd parties
        AwsModule.class,
        RabbitMQModule.class,
        SandalphonClientModule.class,
        GabrielClientModule.class,

        // Features
        SubmissionModule.class,
        ItemSubmissionModule.class,
        StatsModule.class
})
@Singleton
public interface JerahmeelComponent {
    ArchiveResource archiveResource();
    CurriculumResource curriculumResource();
    CourseResource courseResource();
    ChapterResource chapterResource();
    CourseChapterResource courseChapterResource();
    ChapterLessonResource chapterLessonResource();
    ChapterProblemResource chapterProblemResource();
    ProblemResource problemResource();
    ProblemTagResource problemTagResource();
    ProblemSetResource problemSetResource();
    ProblemSetProblemResource problemSetProblemResource();
    ItemSubmissionResource itemSubmissionResource();
    SubmissionResource submissionResource();
    UserStatsResource userStatsResource();

    JudgelsScheduler scheduler();
    GradingResponsePoller gradingResponsePoller();
    ProblemSetStatsTask problemSetStatsTask();
    ContestStatsTask contestStatsTask();
}
