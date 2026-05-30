package tlx.training;

import dagger.Component;
import judgels.service.JudgelsModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.service.persistence.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.hibernate.JudgelsServerHibernateDaoModule;
import tlx.TlxScope;
import tlx.archive.ArchiveStore;
import tlx.chapter.ChapterStore;
import tlx.chapter.problem.ChapterProblemStore;
import tlx.course.CourseStore;
import tlx.course.chapter.CourseChapterStore;
import tlx.problemset.ProblemSetStore;
import tlx.problemset.problem.ProblemSetProblemStore;
import tlx.stats.StatsStore;
import tlx.training.submission.bundle.TrainingItemSubmissionModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JudgelsServerHibernateDaoModule.class,
        TrainingItemSubmissionModule.class})
@TlxScope
public interface TrainingIntegrationTestComponent {
    CourseStore courseStore();
    CourseChapterStore courseChapterStore();
    ChapterStore chapterStore();
    ChapterProblemStore chapterProblemStore();
    ArchiveStore archiveStore();
    ProblemSetStore problemSetStore();
    ProblemSetProblemStore problemSetProblemStore();
    StatsStore statsStore();
    tlx.training.submission.programming.StatsProcessor programmingStatsProcessor();
    tlx.training.submission.bundle.StatsProcessor bundleStatsProcessor();
}
