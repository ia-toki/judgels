package judgels.persistence.hibernate.dao;

import dagger.Module;
import dagger.Provides;
import judgels.contrib.hibernate.UserRegistrationEmailHibernateDao;
import judgels.contrib.persistence.UserRegistrationEmailDao;
import judgels.persistence.dao.ArchiveDao;
import judgels.persistence.dao.BundleGradingDao;
import judgels.persistence.dao.BundleItemSubmissionDao;
import judgels.persistence.dao.BundleSubmissionDao;
import judgels.persistence.dao.ChapterDao;
import judgels.persistence.dao.ChapterLessonDao;
import judgels.persistence.dao.ChapterProblemDao;
import judgels.persistence.dao.ContestAnnouncementDao;
import judgels.persistence.dao.ContestBundleItemSubmissionDao;
import judgels.persistence.dao.ContestClarificationDao;
import judgels.persistence.dao.ContestContestantDao;
import judgels.persistence.dao.ContestDao;
import judgels.persistence.dao.ContestLogDao;
import judgels.persistence.dao.ContestManagerDao;
import judgels.persistence.dao.ContestModuleDao;
import judgels.persistence.dao.ContestProblemDao;
import judgels.persistence.dao.ContestProgrammingGradingDao;
import judgels.persistence.dao.ContestProgrammingSubmissionDao;
import judgels.persistence.dao.ContestRoleDao;
import judgels.persistence.dao.ContestScoreboardDao;
import judgels.persistence.dao.ContestStyleDao;
import judgels.persistence.dao.ContestSupervisorDao;
import judgels.persistence.dao.CourseChapterDao;
import judgels.persistence.dao.CourseDao;
import judgels.persistence.dao.CurriculumDao;
import judgels.persistence.dao.LessonDao;
import judgels.persistence.dao.LessonPartnerDao;
import judgels.persistence.dao.ProblemContestDao;
import judgels.persistence.dao.ProblemDao;
import judgels.persistence.dao.ProblemLevelDao;
import judgels.persistence.dao.ProblemPartnerDao;
import judgels.persistence.dao.ProblemSetDao;
import judgels.persistence.dao.ProblemSetProblemDao;
import judgels.persistence.dao.ProblemSetterDao;
import judgels.persistence.dao.ProblemTagDao;
import judgels.persistence.dao.ProgrammingGradingDao;
import judgels.persistence.dao.ProgrammingSubmissionDao;
import judgels.persistence.dao.SessionDao;
import judgels.persistence.dao.StatsUserDao;
import judgels.persistence.dao.StatsUserProblemDao;
import judgels.persistence.dao.TrainingProgrammingGradingDao;
import judgels.persistence.dao.TrainingProgrammingSubmissionDao;
import judgels.persistence.dao.UserDao;
import judgels.persistence.dao.UserInfoDao;
import judgels.persistence.dao.UserRatingDao;
import judgels.persistence.dao.UserRatingEventDao;
import judgels.persistence.dao.UserResetPasswordDao;
import judgels.persistence.dao.UserRoleDao;

@Module
public class JudgelsServerHibernateDaoModule {
    private JudgelsServerHibernateDaoModule() {}

    @Provides
    static SessionDao sessionDao(SessionHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserDao userDao(UserHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserInfoDao userInfoDao(UserInfoHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRatingDao userRatingDao(UserRatingHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRatingEventDao userRatingEventDao(UserRatingEventHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserResetPasswordDao userResetPasswordDao(UserResetPasswordHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRoleDao userRoleDao(UserRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static UserRegistrationEmailDao userRegistrationEmailDao(UserRegistrationEmailHibernateDao dao) {
        return dao;
    }

    @Provides
    static BundleGradingDao bundleGradingDao(BundleGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static BundleSubmissionDao bundleSubmissionHibernateDao(BundleSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static LessonDao lessonDao(LessonHibernateDao dao) {
        return dao;
    }

    @Provides
    static LessonPartnerDao lessonPartnerDao(LessonPartnerHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemDao problemDao(ProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemPartnerDao problemPartnerDao(ProblemPartnerHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemSetterDao problemSetterDao(ProblemSetterHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemTagDao problemTagDao(ProblemTagHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProgrammingGradingDao programmingGradingDao(ProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProgrammingSubmissionDao programmingSubmissionDao(ProgrammingSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestDao contestDao(ContestHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestStyleDao contestStyleDao(ContestStyleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestAnnouncementDao contestAnnouncementDao(ContestAnnouncementHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestClarificationDao contestClarificationDao(ContestClarificationHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestContestantDao contestContestantDao(ContestContestantHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestManagerDao contestManagerDao(ContestManagerHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestModuleDao contestModuleDao(ContestModuleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProblemDao contestProblemDao(ContestProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestRoleDao contestRoleDao(ContestRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestScoreboardDao contestScoreboardDao(ContestScoreboardHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestSupervisorDao contestSupervisorDao(ContestSupervisorHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestBundleItemSubmissionDao contestBundleItemSubmissionDao(ContestBundleItemSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProgrammingGradingDao contestProgrammingGradingDao(ContestProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProgrammingSubmissionDao contestProgrammingSubmissionDao(
            ContestProgrammingSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestLogDao contestLogDao(ContestLogHibernateDao dao) {
        return dao;
    }

    @Provides
    static ArchiveDao archiveDao(ArchiveHibernateDao dao) {
        return dao;
    }

    @Provides
    static CurriculumDao curriculumDao(CurriculumHibernateDao dao) {
        return dao;
    }

    @Provides
    static CourseDao courseDao(CourseHibernateDao dao) {
        return dao;
    }

    @Provides
    static CourseChapterDao courseChapterDao(CourseChapterHibernateDao dao) {
        return dao;
    }

    @Provides
    static ChapterDao chapterDao(ChapterHibernateDao dao) {
        return dao;
    }

    @Provides
    static ChapterLessonDao chapterLessonDao(ChapterLessonHibernateDao dao) {
        return dao;
    }

    @Provides
    static ChapterProblemDao chapterProblemDao(ChapterProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemContestDao problemContestDao(ProblemContestHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemLevelDao problemLevelDao(ProblemLevelHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemSetDao problemSetDao(ProblemSetHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemSetProblemDao problemSetProblemDao(ProblemSetProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static BundleItemSubmissionDao bundleItemSubmissionDao(BundleItemSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static StatsUserDao statsUserDao(StatsUserHibernateDao dao) {
        return dao;
    }

    @Provides
    static StatsUserProblemDao statsUserProblemDao(StatsUserProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static TrainingProgrammingGradingDao trainingProgrammingGradingDao(TrainingProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static TrainingProgrammingSubmissionDao trainingProgrammingSubmissionDao(TrainingProgrammingSubmissionHibernateDao dao) {
        return dao;
    }
}
