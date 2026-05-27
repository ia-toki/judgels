package judgels.training;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.archive.ArchiveStore;
import judgels.chapter.ChapterStore;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.course.CourseStore;
import judgels.course.chapter.CourseChapterStore;
import judgels.persistence.hibernate.JudgelsHibernateModule;
import judgels.persistence.hibernate.dao.JudgelsServerHibernateDaoModule;
import judgels.problemset.ProblemSetStore;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.service.JudgelsModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.stats.StatsStore;
import judgels.training.submission.bundle.TrainingItemSubmissionModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JudgelsServerHibernateDaoModule.class,
        TrainingItemSubmissionModule.class})
@Singleton
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
