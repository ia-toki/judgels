package judgels.training;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.core.JudgelsModule;
import judgels.persistence.JudgelsHibernateModule;
import judgels.persistence.JudgelsPersistenceModule;
import judgels.training.archive.ArchiveStore;
import judgels.training.chapter.ChapterStore;
import judgels.training.chapter.problem.ChapterProblemStore;
import judgels.training.course.CourseStore;
import judgels.training.course.chapter.CourseChapterStore;
import judgels.training.curriculum.CurriculumStore;
import judgels.training.problemset.ProblemSetStore;
import judgels.training.problemset.problem.ProblemSetProblemStore;
import judgels.training.stats.StatsStore;
import judgels.training.submission.bundle.TrainingItemSubmissionModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        TrainingItemSubmissionModule.class})
@Singleton
public interface TrainingIntegrationTestComponent {
    CurriculumStore curriculumStore();
    CourseStore courseStore();
    CourseChapterStore courseChapterStore();
    ChapterStore chapterStore();
    ChapterProblemStore chapterProblemStore();
    ArchiveStore archiveStore();
    ProblemSetStore problemSetStore();
    ProblemSetProblemStore problemSetProblemStore();
    StatsStore statsStore();
    judgels.training.stats.SubmissionStatsProcessor programmingStatsProcessor();
    judgels.training.stats.ItemSubmissionStatsProcessor bundleStatsProcessor();
}
