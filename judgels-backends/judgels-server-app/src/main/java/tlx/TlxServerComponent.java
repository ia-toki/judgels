package tlx;

import dagger.Subcomponent;
import judgels.submission.programming.GradingResponsePoller;
import judgels.training.submission.bundle.TrainingItemSubmissionModule;
import judgels.training.submission.programming.TrainingGradingResponsePoller;
import judgels.training.submission.programming.TrainingSubmissionModule;
import tlx.recaptcha.RecaptchaModule;
import tlx.tasks.TlxTaskModule;
import tlx.user.registration.UserRegistrationModule;

@Subcomponent(modules = {
        RecaptchaModule.class,
        UserRegistrationModule.class,
        TrainingSubmissionModule.class,
        TrainingItemSubmissionModule.class,
        TlxTaskModule.class})
@TlxScope
public interface TlxServerComponent {
    @Subcomponent.Factory
    interface Factory {
        TlxServerComponent create(
                RecaptchaModule recaptchaModule,
                UserRegistrationModule userRegistrationModule,
                TrainingSubmissionModule trainingSubmissionModule,
                TrainingItemSubmissionModule trainingItemSubmissionModule);
    }

    // Users
    tlx.session.SessionWithGoogleResource sessionWithGoogleResource();
    tlx.user.account.UserAccountWithRegistrationResource userAccountWithRegistrationResource();
    tlx.user.registration.web.UserRegistrationWebResource userRegistrationWebResource();

    // Contests
    tlx.contest.rating.ContestRatingResource contestRatingResource();
    tlx.admin.contest.rating.ContestRatingAdminResource contestRatingAdminResource();
    judgels.admin.user.rating.UserRatingAdminResource userRatingAdminResource();
    tlx.tasks.ReplaceProblemTask tlxReplaceProblemTask();

    // Training
    tlx.archive.ArchiveResource archiveResource();
    tlx.admin.archive.ArchiveAdminResource archiveAdminResource();
    tlx.curriculum.CurriculumResource curriculumResource();
    tlx.course.CourseResource courseResource();
    tlx.admin.course.CourseAdminResource courseAdminResource();
    tlx.chapter.ChapterResource chapterResource();
    tlx.admin.chapter.ChapterAdminResource chapterAdminResource();
    tlx.course.chapter.CourseChapterResource courseChapterResource();
    tlx.admin.course.chapter.CourseChapterAdminResource courseChapterAdminResource();
    tlx.chapter.lesson.ChapterLessonResource chapterLessonResource();
    tlx.admin.chapter.lesson.ChapterLessonAdminResource chapterLessonAdminResource();
    tlx.chapter.problem.ChapterProblemResource chapterProblemResource();
    tlx.admin.chapter.problem.ChapterProblemAdminResource chapterProblemAdminResource();
    tlx.problem.ProblemResource problemResource();
    tlx.problem.ProblemTagResource problemTagResource();
    tlx.problemset.ProblemSetResource problemSetResource();
    tlx.admin.problemset.ProblemSetAdminResource problemSetAdminResource();
    tlx.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
    tlx.admin.problemset.problem.ProblemSetProblemAdminResource problemSetProblemAdminResource();
    tlx.submission.bundle.ItemSubmissionResource itemSubmissionResource();
    tlx.submission.programming.SubmissionResource submissionResource();
    tlx.stats.UserStatsResource userStatsResource();
    @TrainingGradingResponsePoller GradingResponsePoller trainingGradingResponsePoller();
    tlx.tasks.RefreshContestStatsTask refreshContestStatsTask();
    tlx.tasks.RefreshProblemSetStatsTask refreshProblemSetStatsTask();
    tlx.tasks.DeleteProblemTask tlxDeleteProblemTask();
    tlx.tasks.MoveProblemToChapterTask tlxMoveProblemToChapterTask();
    tlx.tasks.MoveProblemToProblemSetTask tlxMoveProblemToProblemSetTask();
}
