package judgels.sandalphon;

import dagger.Component;
import javax.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.sandalphon.lesson.LessonResource;
import judgels.sandalphon.problem.base.ProblemResource;
import judgels.service.JudgelsModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsPersistenceModule.class,
        JudgelsServerModule.class,

        JudgelsHibernateModule.class,
        SandalphonHibernateDaoModule.class,

        SandalphonModule.class})
@Singleton
public interface SandalphonComponent {
    ProblemResource problemResource();
    LessonResource lessonResource();
}
