package judgels.sandalphon;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.lesson.LessonResource;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.problem.base.ProblemResource;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.gabriel.GabrielClientModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.submission.SubmissionModule;
import judgels.submission.programming.GradingResponsePoller;

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
