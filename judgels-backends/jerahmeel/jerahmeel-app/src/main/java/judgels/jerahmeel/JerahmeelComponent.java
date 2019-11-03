package judgels.jerahmeel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.fs.aws.AwsModule;
import judgels.jerahmeel.admin.AdminResource;
import judgels.jerahmeel.chapter.lesson.ChapterLessonResource;
import judgels.jerahmeel.chapter.problem.ChapterProblemResource;
import judgels.jerahmeel.chapter.submission.ChapterSubmissionResource;
import judgels.jerahmeel.course.CourseResource;
import judgels.jerahmeel.course.chapter.CourseChapterResource;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jerahmeel.jophiel.JophielModule;
import judgels.jerahmeel.sandalphon.SandalphonModule;
import judgels.jerahmeel.submission.programming.SubmissionModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        AwsModule.class,
        SubmissionModule.class,

        JophielModule.class,
        SandalphonModule.class,

        JudgelsModule.class,
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
    PingResource pingResource();
}
