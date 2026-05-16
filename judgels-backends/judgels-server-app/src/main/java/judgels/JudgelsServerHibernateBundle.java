package judgels;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.persistence.ArchiveModel;
import judgels.persistence.BundleGradingModel;
import judgels.persistence.BundleItemSubmissionModel;
import judgels.persistence.BundleSubmissionModel;
import judgels.persistence.ChapterLessonModel;
import judgels.persistence.ChapterModel;
import judgels.persistence.ChapterProblemModel;
import judgels.persistence.ContestAnnouncementModel;
import judgels.persistence.ContestBundleItemSubmissionModel;
import judgels.persistence.ContestClarificationModel;
import judgels.persistence.ContestContestantModel;
import judgels.persistence.ContestLogModel;
import judgels.persistence.ContestManagerModel;
import judgels.persistence.ContestModel;
import judgels.persistence.ContestModuleModel;
import judgels.persistence.ContestProblemModel;
import judgels.persistence.ContestProgrammingGradingModel;
import judgels.persistence.ContestProgrammingSubmissionModel;
import judgels.persistence.ContestScoreboardModel;
import judgels.persistence.ContestStyleModel;
import judgels.persistence.ContestSupervisorModel;
import judgels.persistence.CourseChapterModel;
import judgels.persistence.CourseModel;
import judgels.persistence.CurriculumModel;
import judgels.persistence.LessonModel;
import judgels.persistence.LessonPartnerModel;
import judgels.persistence.ProblemContestModel;
import judgels.persistence.ProblemLevelModel;
import judgels.persistence.ProblemModel;
import judgels.persistence.ProblemPartnerModel;
import judgels.persistence.ProblemSetModel;
import judgels.persistence.ProblemSetProblemModel;
import judgels.persistence.ProblemSetterModel;
import judgels.persistence.ProblemTagModel;
import judgels.persistence.SessionModel;
import judgels.persistence.StatsUserModel;
import judgels.persistence.StatsUserProblemModel;
import judgels.persistence.UserInfoModel;
import judgels.persistence.UserModel;
import judgels.persistence.UserRatingModel;
import judgels.persistence.UserResetPasswordModel;
import judgels.persistence.UserRoleModel;

public class JudgelsServerHibernateBundle extends HibernateBundle<JudgelsServerApplicationConfiguration> {
    public JudgelsServerHibernateBundle() {
        super(
                // Jophiel
                SessionModel.class,
                UserModel.class,
                UserInfoModel.class,
                UserRatingModel.class,
                UserResetPasswordModel.class,
                UserRoleModel.class,
                judgels.persistence.UserRatingEventModel.class,
                judgels.contrib.persistence.UserRegistrationEmailModel.class,

                // Sandalphon
                BundleGradingModel.class,
                BundleSubmissionModel.class,
                LessonModel.class,
                LessonPartnerModel.class,
                ProblemModel.class,
                ProblemPartnerModel.class,
                ProblemSetterModel.class,
                ProblemTagModel.class,
                judgels.persistence.ProgrammingGradingModel.class,
                judgels.persistence.ProgrammingSubmissionModel.class,

                // Uriel
                ContestModel.class,
                ContestAnnouncementModel.class,
                ContestBundleItemSubmissionModel.class,
                ContestClarificationModel.class,
                ContestContestantModel.class,
                ContestManagerModel.class,
                ContestModuleModel.class,
                ContestProblemModel.class,
                ContestProgrammingGradingModel.class,
                ContestProgrammingSubmissionModel.class,
                ContestScoreboardModel.class,
                ContestStyleModel.class,
                ContestSupervisorModel.class,
                ContestLogModel.class,

                // Jerahmeel
                ArchiveModel.class,
                CurriculumModel.class,
                ChapterModel.class,
                ChapterLessonModel.class,
                ChapterProblemModel.class,
                CourseModel.class,
                CourseChapterModel.class,
                judgels.persistence.TrainingProgrammingGradingModel.class,
                judgels.persistence.TrainingProgrammingSubmissionModel.class,
                BundleItemSubmissionModel.class,
                ProblemContestModel.class,
                ProblemLevelModel.class,
                ProblemSetModel.class,
                ProblemSetProblemModel.class,
                StatsUserModel.class,
                StatsUserProblemModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JudgelsServerApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
