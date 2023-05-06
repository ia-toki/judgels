package judgels.michael;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateDaoModule;
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
import judgels.sandalphon.SandalphonModule;
import judgels.sandalphon.hibernate.SandalphonHibernateDaoModule;
import judgels.sandalphon.submission.SandalphonSubmissionModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsSchedulerModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JudgelsSchedulerModule.class,

        // Jophiel
        JophielHibernateDaoModule.class,

        // Sandalphon
        SandalphonHibernateDaoModule.class,
        SandalphonModule.class,
        SandalphonSubmissionModule.class})
@Singleton
public interface MichaelComponent {
    IndexResource indexResource();
    ProblemResource problemResource();
    ProblemStatementResource problemStatementResource();
    ProblemStatementRenderResources.InEditProblemStatement problemStatementRenderResourceInEditProblemStatement();
    ProblemStatementRenderResources.InViewProgrammingProblemStatement problemStatementRenderResourceInViewProgrammingProblemStatement();
    ProblemStatementRenderResources.InViewBundleProblemStatement problemStatementRenderResourceInViewBundleProblemStatement();
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
