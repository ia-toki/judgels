package judgels.sandalphon;

import dagger.Component;
import javax.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.sandalphon.lesson.LessonResource;
import judgels.sandalphon.problem.base.ProblemResource;
import judgels.sandalphon.submission.SubmissionModule;
import judgels.sandalphon.submission.programming.GradingResponsePoller;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.gabriel.GabrielClientModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsServerModule.class,
        JudgelsPersistenceModule.class,
        JudgelsSchedulerModule.class,

        // Database
        JudgelsHibernateModule.class,
        SandalphonHibernateDaoModule.class,

        // 3rd parties
        RabbitMQModule.class,
        SandalphonClientModule.class,
        GabrielClientModule.class,

        // Features
        SubmissionModule.class})
@Singleton
public interface SandalphonComponent {
    ProblemResource problemResource();
    LessonResource lessonResource();

    JudgelsScheduler scheduler();
    GradingResponsePoller gradingResponsePoller();
}
