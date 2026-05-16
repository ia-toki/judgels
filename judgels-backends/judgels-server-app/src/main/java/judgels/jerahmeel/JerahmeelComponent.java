package judgels.jerahmeel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.admin.archive.ArchiveAdminResource;
import judgels.admin.chapter.ChapterAdminResource;
import judgels.admin.chapter.lesson.ChapterLessonAdminResource;
import judgels.admin.chapter.problem.ChapterProblemAdminResource;
import judgels.admin.course.CourseAdminResource;
import judgels.admin.course.chapter.CourseChapterAdminResource;
import judgels.admin.problemset.ProblemSetAdminResource;
import judgels.admin.problemset.problem.ProblemSetProblemAdminResource;
import judgels.archive.ArchiveResource;
import judgels.chapter.ChapterResource;
import judgels.chapter.lesson.ChapterLessonResource;
import judgels.chapter.problem.ChapterProblemResource;
import judgels.course.CourseResource;
import judgels.course.chapter.CourseChapterResource;
import judgels.curriculum.CurriculumResource;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.problem.ProblemResource;
import judgels.problem.ProblemTagResource;
import judgels.problemset.ProblemSetResource;
import judgels.problemset.problem.ProblemSetProblemResource;
import judgels.sandalphon.SandalphonClientModule;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.gabriel.GabrielClientModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.stats.UserStatsResource;
import judgels.submission.bundle.ArchiveItemSubmissionModule;
import judgels.submission.bundle.ItemSubmissionResource;
import judgels.submission.programming.ArchiveSubmissionModule;
import judgels.submission.programming.GradingResponsePoller;
import judgels.submission.programming.SubmissionResource;
import judgels.tasks.JerahmeelTaskModule;
import judgels.tasks.RefreshContestStatsTask;
import judgels.tasks.RefreshProblemSetStatsTask;
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
        RabbitMQModule.class,
        SandalphonClientModule.class,
        GabrielClientModule.class,

        // Features
        ArchiveSubmissionModule.class,
        ArchiveItemSubmissionModule.class,
        JerahmeelTaskModule.class,

        tlx.jerahmeel.tasks.TlxJerahmeelTaskModule.class})
@Singleton
public interface JerahmeelComponent {
    ArchiveResource archiveResource();
    ArchiveAdminResource archiveAdminResource();
    CurriculumResource curriculumResource();
    CourseResource courseResource();
    CourseAdminResource courseAdminResource();
    ChapterResource chapterResource();
    ChapterAdminResource chapterAdminResource();
    CourseChapterResource courseChapterResource();
    CourseChapterAdminResource courseChapterAdminResource();
    ChapterLessonResource chapterLessonResource();
    ChapterLessonAdminResource chapterLessonAdminResource();
    ChapterProblemResource chapterProblemResource();
    ChapterProblemAdminResource chapterProblemAdminResource();
    ProblemResource problemResource();
    ProblemTagResource problemTagResource();
    ProblemSetResource problemSetResource();
    ProblemSetAdminResource problemSetAdminResource();
    ProblemSetProblemResource problemSetProblemResource();
    ProblemSetProblemAdminResource problemSetProblemAdminResource();
    ItemSubmissionResource itemSubmissionResource();
    SubmissionResource submissionResource();
    UserStatsResource userStatsResource();

    JudgelsScheduler scheduler();
    GradingResponsePoller gradingResponsePoller();

    RefreshContestStatsTask refreshContestStatsTask();
    RefreshProblemSetStatsTask refreshProblemSetStatsTask();

    tlx.jerahmeel.tasks.DeleteProblemTask tlxDeleteProblemTask();
    tlx.jerahmeel.tasks.MoveProblemToChapterTask tlxMoveProblemToChapterTask();
    tlx.jerahmeel.tasks.MoveProblemToProblemSetTask tlxMoveProblemToProblemSetTask();
}
