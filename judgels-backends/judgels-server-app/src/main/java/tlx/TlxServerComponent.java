package tlx;

import dagger.Subcomponent;
import judgels.submission.programming.GradingResponsePoller;
import tlx.mailer.MailerModule;
import tlx.recaptcha.RecaptchaModule;
import tlx.tasks.TlxTaskModule;
import tlx.training.submission.bundle.TrainingItemSubmissionModule;
import tlx.training.submission.programming.TrainingGradingResponsePoller;
import tlx.training.submission.programming.TrainingSubmissionModule;
import tlx.user.account.UserResetPasswordModule;
import tlx.user.registration.UserRegistrationModule;

@Subcomponent(modules = {
        MailerModule.class,
        RecaptchaModule.class,
        UserRegistrationModule.class,
        UserResetPasswordModule.class,
        TrainingSubmissionModule.class,
        TrainingItemSubmissionModule.class,
        TlxTaskModule.class})
@TlxScope
public interface TlxServerComponent {
    @Subcomponent.Factory
    interface Factory {
        TlxServerComponent create(
                MailerModule mailerModule,
                RecaptchaModule recaptchaModule,
                UserRegistrationModule userRegistrationModule,
                UserResetPasswordModule userResetPasswordModule,
                TrainingSubmissionModule trainingSubmissionModule,
                TrainingItemSubmissionModule trainingItemSubmissionModule);
    }

    tlx.session.TlxSessionResource sessionResource();
    tlx.user.account.UserAccountResource userAccountResource();
    tlx.user.registration.web.UserRegistrationWebResource userRegistrationWebResource();
    tlx.user.rating.UserRatingResource userRatingResource();
    tlx.contest.rating.ContestRatingResource contestRatingResource();
    tlx.curriculum.CurriculumResource curriculumResource();
    tlx.archive.ArchiveResource archiveResource();
    tlx.course.CourseResource courseResource();
    tlx.chapter.ChapterResource chapterResource();
    tlx.course.chapter.CourseChapterResource courseChapterResource();
    tlx.chapter.lesson.ChapterLessonResource chapterLessonResource();
    tlx.chapter.problem.ChapterProblemResource chapterProblemResource();
    tlx.problem.ProblemResource problemResource();
    tlx.problemset.ProblemSetResource problemSetResource();
    tlx.problemset.problem.ProblemSetProblemResource problemSetProblemResource();
    tlx.submission.bundle.ItemSubmissionResource itemSubmissionResource();
    tlx.submission.programming.SubmissionResource submissionResource();
    tlx.stats.UserStatsResource userStatsResource();

    @TrainingGradingResponsePoller GradingResponsePoller trainingGradingResponsePoller();

    tlx.tasks.DeleteTrainingProblemTask deleteTrainingProblemTask();
    tlx.tasks.MoveTrainingProblemToChapterTask moveTrainingProblemToChapterTask();
    tlx.tasks.MoveTrainingProblemToProblemSetTask moveTrainingProblemToProblemSetTask();
    tlx.tasks.RefreshContestStatsTask refreshContestStatsTask();
    tlx.tasks.RefreshProblemSetStatsTask refreshProblemSetStatsTask();
    tlx.tasks.ReplaceContestProblemTask replaceContestProblemTask();

}
