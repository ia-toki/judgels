package tlx;

import dagger.Subcomponent;
import judgels.submission.programming.GradingResponsePoller;
import tlx.recaptcha.RecaptchaModule;
import tlx.tasks.TlxTaskModule;
import tlx.training.submission.bundle.TrainingItemSubmissionModule;
import tlx.training.submission.programming.TrainingGradingResponsePoller;
import tlx.training.submission.programming.TrainingSubmissionModule;
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
    tlx.user.rating.UserRatingResource userRatingResource();
    tlx.tasks.ReplaceProblemTask tlxReplaceProblemTask();

    // Training
    tlx.archive.ArchiveResource archiveResource();
    tlx.curriculum.CurriculumResource curriculumResource();
    tlx.course.CourseResource courseResource();
    tlx.chapter.ChapterResource chapterResource();
    tlx.course.chapter.CourseChapterResource courseChapterResource();
    tlx.chapter.lesson.ChapterLessonResource chapterLessonResource();
    tlx.chapter.problem.ChapterProblemResource chapterProblemResource();
    tlx.problem.ProblemResource problemResource();
    tlx.problem.ProblemTagResource problemTagResource();
    tlx.problemset.ProblemSetResource problemSetResource();
    tlx.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
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
