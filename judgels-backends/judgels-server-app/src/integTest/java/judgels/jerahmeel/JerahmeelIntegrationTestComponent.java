package judgels.jerahmeel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.archive.ArchiveStore;
import judgels.chapter.ChapterStore;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.course.CourseStore;
import judgels.course.chapter.CourseChapterStore;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.problemset.ProblemSetStore;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.service.JudgelsModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.stats.StatsStore;
import judgels.submission.bundle.TrainingItemSubmissionModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JerahmeelHibernateDaoModule.class,
        TrainingItemSubmissionModule.class})
@Singleton
public interface JerahmeelIntegrationTestComponent {
    CourseStore courseStore();
    CourseChapterStore courseChapterStore();
    ChapterStore chapterStore();
    ChapterProblemStore chapterProblemStore();
    ArchiveStore archiveStore();
    ProblemSetStore problemSetStore();
    ProblemSetProblemStore problemSetProblemStore();
    StatsStore statsStore();
    judgels.submission.programming.StatsProcessor programmingStatsProcessor();
    judgels.submission.bundle.StatsProcessor bundleStatsProcessor();
}
