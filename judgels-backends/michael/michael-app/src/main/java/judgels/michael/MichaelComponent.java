package judgels.michael;

import dagger.Component;
import javax.inject.Singleton;
import judgels.fs.aws.AwsModule;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
import judgels.jophiel.user.avatar.UserAvatarModule;
import judgels.michael.index.IndexResource;
import judgels.michael.lesson.LessonResource;
import judgels.michael.problem.base.ProblemResource;
import judgels.michael.problem.programming.ProgrammingProblemStatementResource;
import judgels.sandalphon.SandalphonModule;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.JudgelsScheduler;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsApplicationModule.class,
        JudgelsPersistenceModule.class,
        JudgelsHibernateModule.class,
        MichaelModule.class,

        // Jophiel
        JophielHibernateDaoModule.class,
        AwsModule.class,
        UserAvatarModule.class,

        // Sandalphon
        SandalphonHibernateDaoModule.class,
        SandalphonModule.class})
@Singleton
public interface MichaelComponent {
    PingResource pingResource();
    IndexResource indexResource();
    ProblemResource problemResource();
    ProgrammingProblemStatementResource programmingProblemResource();
    LessonResource lessonResource();

    JudgelsScheduler scheduler();
}
