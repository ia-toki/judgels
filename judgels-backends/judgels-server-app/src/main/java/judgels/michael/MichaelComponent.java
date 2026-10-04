package judgels.michael;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.JudgelsServerModule;
import judgels.auth.AuthModule;
import judgels.catalog.CatalogModule;
import judgels.core.JudgelsModule;
import judgels.core.JudgelsSchedulerModule;
import judgels.core.messaging.rabbitmq.RabbitMQModule;
import judgels.grading.GradingModule;
import judgels.michael.index.IndexResource;
import judgels.michael.lesson.LessonResource;
import judgels.michael.lesson.partner.LessonPartnerResource;
import judgels.michael.lesson.render.LessonStatementRenderResources;
import judgels.michael.lesson.statement.LessonStatementResource;
import judgels.michael.lesson.version.LessonVersionResource;
import judgels.michael.problem.ProblemResource;
import judgels.michael.problem.bundle.item.BundleProblemItemResource;
import judgels.michael.problem.bundle.statement.BundleProblemStatementResource;
import judgels.michael.problem.bundle.submission.BundleProblemSubmissionResource;
import judgels.michael.problem.editorial.ProblemEditorialResource;
import judgels.michael.problem.partner.ProblemPartnerResource;
import judgels.michael.problem.programming.grading.ProgrammingProblemGradingResource;
import judgels.michael.problem.programming.statement.ProgrammingProblemStatementResource;
import judgels.michael.problem.programming.submission.ProgrammingProblemSubmissionResource;
import judgels.michael.problem.render.ProblemEditorialRenderResources;
import judgels.michael.problem.render.ProblemStatementRenderResources;
import judgels.michael.problem.statement.ProblemStatementResource;
import judgels.michael.problem.version.ProblemVersionResource;
import judgels.persistence.JudgelsHibernateModule;
import judgels.persistence.JudgelsPersistenceModule;
import judgels.submission.SubmissionModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsServerModule.class,
        JudgelsPersistenceModule.class,
        JudgelsSchedulerModule.class,

        // Database
        JudgelsHibernateModule.class,

        // 3rd parties
        AuthModule.class,
        RabbitMQModule.class,
        GradingModule.class,
        CatalogModule.class,

        // Features
        SubmissionModule.class})
@Singleton
public interface MichaelComponent {
    IndexResource indexResource();
    ProblemResource problemResource();
    ProblemStatementResource problemStatementResource();
    ProblemStatementRenderResources.InEditProblemStatement problemStatementRenderResourceInEditProblemStatement();
    ProblemStatementRenderResources.InViewProgrammingProblemStatement problemStatementRenderResourceInViewProgrammingProblemStatement();
    ProblemStatementRenderResources.InViewBundleProblemStatement problemStatementRenderResourceInViewBundleProblemStatement();
    ProblemStatementRenderResources.InViewBundleItemProblemStatement problemStatementRenderResourceInViewBundleItemProblemStatement();
    ProblemPartnerResource problemPartnerResource();
    ProblemEditorialResource problemEditorialResource();
    ProblemEditorialRenderResources.InEditProblemEditorial problemEditorialRenderResourceInEditProblemEditorial();
    ProblemEditorialRenderResources.InViewProblemEditorial problemEditorialRenderResourceInViewProblemEditorial();
    ProblemVersionResource problemVersionResource();
    ProgrammingProblemStatementResource programmingProblemStatementResource();
    ProgrammingProblemGradingResource programmingProblemGradingResource();
    ProgrammingProblemSubmissionResource programmingProblemSubmissionResource();
    BundleProblemStatementResource bundleProblemStatementResource();
    BundleProblemItemResource bundleProblemItemResource();
    BundleProblemSubmissionResource bundleProblemSubmissionResource();
    LessonResource lessonResource();
    LessonStatementResource lessonStatementResource();
    LessonStatementRenderResources.InEditLessonStatement lessonStatementRenderResourceInEditLessonStatement();
    LessonStatementRenderResources.InViewLessonStatement lessonStatementRenderResourceInViewLessonStatement();
    LessonPartnerResource lessonPartnerResource();
    LessonVersionResource lessonVersionResource();
}
