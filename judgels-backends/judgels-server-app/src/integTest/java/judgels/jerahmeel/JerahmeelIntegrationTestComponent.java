package judgels.jerahmeel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jerahmeel.archive.ArchiveStore;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.course.CourseStore;
import judgels.jerahmeel.course.chapter.CourseChapterStore;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jerahmeel.submission.bundle.ItemSubmissionModule;
import judgels.service.JudgelsModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JerahmeelHibernateDaoModule.class,
        ItemSubmissionModule.class})
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
    judgels.jerahmeel.submission.programming.StatsProcessor programmingStatsProcessor();
    judgels.jerahmeel.submission.bundle.StatsProcessor bundleStatsProcessor();
}
