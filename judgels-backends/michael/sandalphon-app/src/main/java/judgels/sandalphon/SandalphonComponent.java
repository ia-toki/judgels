package judgels.sandalphon;

import dagger.Component;
import javax.inject.Singleton;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.sandalphon.lesson.LessonResource;
import judgels.sandalphon.problem.base.ProblemResource;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        SandalphonModule.class,
        SandalphonHibernateDaoModule.class})
@Singleton
public interface SandalphonComponent {
    ProblemResource problemResource();
    LessonResource lessonResource();
}
