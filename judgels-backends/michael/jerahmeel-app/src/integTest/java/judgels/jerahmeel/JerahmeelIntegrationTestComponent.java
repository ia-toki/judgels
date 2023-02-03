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
import judgels.jerahmeel.submission.programming.StatsProcessor;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JerahmeelModule.class,
        JerahmeelHibernateDaoModule.class})
@Singleton
public interface JerahmeelIntegrationTestComponent {
    CourseStore courseStore();
    CourseChapterStore courseChapterStore();
    ChapterStore chapterStore();
    ChapterProblemStore chapterProblemStore();
    ArchiveStore archiveStore();
    ProblemSetStore problemSetStore();
    ProblemSetProblemStore problemSetProblemStore();
    StatsProcessor statsProcessor();
    StatsStore statsStore();
}
