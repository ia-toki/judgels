package judgels.training;

import dagger.Component;
import judgels.archive.ArchiveStore;
import judgels.chapter.ChapterStore;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.course.CourseStore;
import judgels.course.chapter.CourseChapterStore;
import judgels.problemset.ProblemSetStore;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.service.JudgelsModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.service.persistence.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.hibernate.JudgelsServerHibernateDaoModule;
import judgels.stats.StatsStore;
import judgels.training.submission.bundle.TrainingItemSubmissionModule;
import tlx.TlxScope;

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
    judgels.training.submission.programming.StatsProcessor programmingStatsProcessor();
    judgels.training.submission.bundle.StatsProcessor bundleStatsProcessor();
}
