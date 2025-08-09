package judgels;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jerahmeel.persistence.ArchiveModel;
import judgels.jerahmeel.persistence.BundleItemSubmissionModel;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.CurriculumModel;
import judgels.jerahmeel.persistence.ProblemContestModel;
import judgels.jerahmeel.persistence.ProblemLevelModel;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.StatsUserModel;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.UserInfoModel;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.jophiel.persistence.UserRoleModel;
import judgels.sandalphon.persistence.BundleGradingModel;
import judgels.sandalphon.persistence.BundleSubmissionModel;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonPartnerModel;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemSetterModel;
import judgels.sandalphon.persistence.ProblemTagModel;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestLogModel;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProgrammingGradingModel;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestSupervisorModel;

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
                judgels.jophiel.persistence.UserRatingEventModel.class,
                judgels.contrib.jophiel.persistence.UserRegistrationEmailModel.class,

                // Sandalphon
                BundleGradingModel.class,
                BundleSubmissionModel.class,
                LessonModel.class,
                LessonPartnerModel.class,
                ProblemModel.class,
                ProblemPartnerModel.class,
                ProblemSetterModel.class,
                ProblemTagModel.class,
                judgels.sandalphon.persistence.ProgrammingGradingModel.class,
                judgels.sandalphon.persistence.ProgrammingSubmissionModel.class,

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
                judgels.jerahmeel.persistence.ProgrammingGradingModel.class,
                judgels.jerahmeel.persistence.ProgrammingSubmissionModel.class,
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
