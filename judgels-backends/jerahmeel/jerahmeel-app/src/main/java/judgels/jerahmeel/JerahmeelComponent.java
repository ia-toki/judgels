package judgels.jerahmeel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jerahmeel.admin.AdminResource;
import judgels.jerahmeel.chapter.lesson.ChapterLessonResource;
import judgels.jerahmeel.course.CourseResource;
import judgels.jerahmeel.course.chapter.CourseChapterResource;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jerahmeel.jophiel.JophielModule;
import judgels.jerahmeel.sandalphon.SandalphonModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        JerahmeelHibernateDaoModule.class,
        JerahmeelModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JophielModule.class,
        SandalphonModule.class
})
@Singleton
public interface JerahmeelComponent {
    AdminResource adminResource();
    CourseResource courseResource();
    CourseChapterResource courseChapterResource();
    ChapterLessonResource chapterLessonResource();
    PingResource pingResource();
}
