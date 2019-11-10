package judgels.jerahmeel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.fs.aws.AwsModule;
import judgels.jerahmeel.admin.AdminResource;
import judgels.jerahmeel.chapter.lesson.ChapterLessonResource;
import judgels.jerahmeel.chapter.problem.ChapterProblemResource;
import judgels.jerahmeel.chapter.submission.bundle.ChapterItemSubmissionResource;
import judgels.jerahmeel.chapter.submission.programming.ChapterSubmissionResource;
import judgels.jerahmeel.course.CourseResource;
import judgels.jerahmeel.course.chapter.CourseChapterResource;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jerahmeel.jophiel.JophielModule;
import judgels.jerahmeel.sandalphon.SandalphonModule;
import judgels.jerahmeel.submission.bundle.ItemSubmissionModule;
import judgels.jerahmeel.submission.programming.SubmissionModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        AwsModule.class,
        SubmissionModule.class,

        JophielModule.class,
        SandalphonModule.class,
        ItemSubmissionModule.class,

        JudgelsModule.class,
        JudgelsApplicationModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,

        JerahmeelModule.class,
        JerahmeelHibernateDaoModule.class
})
@Singleton
public interface JerahmeelComponent {
    AdminResource adminResource();
    CourseResource courseResource();
    CourseChapterResource courseChapterResource();
    ChapterLessonResource chapterLessonResource();
    ChapterProblemResource chapterProblemResource();
    ChapterSubmissionResource chapterSubmissionResource();
    ChapterItemSubmissionResource chapterItemSubmissionResource();
    PingResource pingResource();
}
