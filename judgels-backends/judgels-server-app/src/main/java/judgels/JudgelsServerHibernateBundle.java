package judgels;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.persistence.model.ArchiveModel;
import judgels.persistence.model.BundleGradingModel;
import judgels.persistence.model.BundleItemSubmissionModel;
import judgels.persistence.model.BundleSubmissionModel;
import judgels.persistence.model.ChapterLessonModel;
import judgels.persistence.model.ChapterModel;
import judgels.persistence.model.ChapterProblemModel;
import judgels.persistence.model.ContestAnnouncementModel;
import judgels.persistence.model.ContestBundleItemSubmissionModel;
import judgels.persistence.model.ContestClarificationModel;
import judgels.persistence.model.ContestContestantModel;
import judgels.persistence.model.ContestLogModel;
import judgels.persistence.model.ContestManagerModel;
import judgels.persistence.model.ContestModel;
import judgels.persistence.model.ContestModuleModel;
import judgels.persistence.model.ContestProblemModel;
import judgels.persistence.model.ContestProgrammingGradingModel;
import judgels.persistence.model.ContestProgrammingSubmissionModel;
import judgels.persistence.model.ContestScoreboardModel;
import judgels.persistence.model.ContestStyleModel;
import judgels.persistence.model.ContestSupervisorModel;
import judgels.persistence.model.CourseChapterModel;
import judgels.persistence.model.CourseModel;
import judgels.persistence.model.CurriculumModel;
import judgels.persistence.model.LessonModel;
import judgels.persistence.model.LessonPartnerModel;
import judgels.persistence.model.ProblemContestModel;
import judgels.persistence.model.ProblemLevelModel;
import judgels.persistence.model.ProblemModel;
import judgels.persistence.model.ProblemPartnerModel;
import judgels.persistence.model.ProblemSetModel;
import judgels.persistence.model.ProblemSetProblemModel;
import judgels.persistence.model.ProblemSetterModel;
import judgels.persistence.model.ProblemTagModel;
import judgels.persistence.model.SessionModel;
import judgels.persistence.model.StatsUserModel;
import judgels.persistence.model.StatsUserProblemModel;
import judgels.persistence.model.UserInfoModel;
import judgels.persistence.model.UserModel;
import judgels.persistence.model.UserRatingModel;
import judgels.persistence.model.UserResetPasswordModel;
import judgels.persistence.model.UserRoleModel;

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
                judgels.persistence.model.UserRatingEventModel.class,
                judgels.persistence.model.UserRegistrationEmailModel.class,

                // Sandalphon
                BundleGradingModel.class,
                BundleSubmissionModel.class,
                LessonModel.class,
                LessonPartnerModel.class,
                ProblemModel.class,
                ProblemPartnerModel.class,
                ProblemSetterModel.class,
                ProblemTagModel.class,
                judgels.persistence.model.ProgrammingGradingModel.class,
                judgels.persistence.model.ProgrammingSubmissionModel.class,

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
                judgels.persistence.model.TrainingProgrammingGradingModel.class,
                judgels.persistence.model.TrainingProgrammingSubmissionModel.class,
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
