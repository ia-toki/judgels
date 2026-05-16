package judgels.service.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.contrib.hibernate.UserRegistrationEmailHibernateDao;
import judgels.contrib.persistence.UserRegistrationEmailDao;
import judgels.hibernate.ArchiveHibernateDao;
import judgels.hibernate.BundleGradingHibernateDao;
import judgels.hibernate.BundleItemSubmissionHibernateDao;
import judgels.hibernate.BundleSubmissionHibernateDao;
import judgels.hibernate.ChapterHibernateDao;
import judgels.hibernate.ChapterLessonHibernateDao;
import judgels.hibernate.ChapterProblemHibernateDao;
import judgels.hibernate.ContestAnnouncementHibernateDao;
import judgels.hibernate.ContestBundleItemSubmissionHibernateDao;
import judgels.hibernate.ContestClarificationHibernateDao;
import judgels.hibernate.ContestContestantHibernateDao;
import judgels.hibernate.ContestHibernateDao;
import judgels.hibernate.ContestLogHibernateDao;
import judgels.hibernate.ContestManagerHibernateDao;
import judgels.hibernate.ContestModuleHibernateDao;
import judgels.hibernate.ContestProblemHibernateDao;
import judgels.hibernate.ContestProgrammingGradingHibernateDao;
import judgels.hibernate.ContestProgrammingSubmissionHibernateDao;
import judgels.hibernate.ContestRoleHibernateDao;
import judgels.hibernate.ContestScoreboardHibernateDao;
import judgels.hibernate.ContestStyleHibernateDao;
import judgels.hibernate.ContestSupervisorHibernateDao;
import judgels.hibernate.CourseChapterHibernateDao;
import judgels.hibernate.CourseHibernateDao;
import judgels.hibernate.CurriculumHibernateDao;
import judgels.hibernate.LessonHibernateDao;
import judgels.hibernate.LessonPartnerHibernateDao;
import judgels.hibernate.ProblemContestHibernateDao;
import judgels.hibernate.ProblemHibernateDao;
import judgels.hibernate.ProblemLevelHibernateDao;
import judgels.hibernate.ProblemPartnerHibernateDao;
import judgels.hibernate.ProblemSetHibernateDao;
import judgels.hibernate.ProblemSetProblemHibernateDao;
import judgels.hibernate.ProblemSetterHibernateDao;
import judgels.hibernate.ProblemTagHibernateDao;
import judgels.hibernate.SessionHibernateDao;
import judgels.hibernate.StatsUserHibernateDao;
import judgels.hibernate.StatsUserProblemHibernateDao;
import judgels.hibernate.UserHibernateDao;
import judgels.hibernate.UserInfoHibernateDao;
import judgels.hibernate.UserRatingEventHibernateDao;
import judgels.hibernate.UserRatingHibernateDao;
import judgels.hibernate.UserResetPasswordHibernateDao;
import judgels.hibernate.UserRoleHibernateDao;
import judgels.persistence.ArchiveDao;
import judgels.persistence.BundleGradingDao;
import judgels.persistence.BundleItemSubmissionDao;
import judgels.persistence.BundleSubmissionDao;
import judgels.persistence.ChapterDao;
import judgels.persistence.ChapterLessonDao;
import judgels.persistence.ChapterProblemDao;
import judgels.persistence.ContestAnnouncementDao;
import judgels.persistence.ContestBundleItemSubmissionDao;
import judgels.persistence.ContestClarificationDao;
import judgels.persistence.ContestContestantDao;
import judgels.persistence.ContestDao;
import judgels.persistence.ContestLogDao;
import judgels.persistence.ContestManagerDao;
import judgels.persistence.ContestModuleDao;
import judgels.persistence.ContestProblemDao;
import judgels.persistence.ContestProgrammingGradingDao;
import judgels.persistence.ContestProgrammingSubmissionDao;
import judgels.persistence.ContestRoleDao;
import judgels.persistence.ContestScoreboardDao;
import judgels.persistence.ContestStyleDao;
import judgels.persistence.ContestSupervisorDao;
import judgels.persistence.CourseChapterDao;
import judgels.persistence.CourseDao;
import judgels.persistence.CurriculumDao;
import judgels.persistence.LessonDao;
import judgels.persistence.LessonPartnerDao;
import judgels.persistence.ProblemContestDao;
import judgels.persistence.ProblemDao;
import judgels.persistence.ProblemLevelDao;
import judgels.persistence.ProblemPartnerDao;
import judgels.persistence.ProblemSetDao;
import judgels.persistence.ProblemSetProblemDao;
import judgels.persistence.ProblemSetterDao;
import judgels.persistence.ProblemTagDao;
import judgels.persistence.SessionDao;
import judgels.persistence.StatsUserDao;
import judgels.persistence.StatsUserProblemDao;
import judgels.persistence.UserDao;
import judgels.persistence.UserInfoDao;
import judgels.persistence.UserRatingDao;
import judgels.persistence.UserRatingEventDao;
import judgels.persistence.UserResetPasswordDao;
import judgels.persistence.UserRoleDao;

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
    static judgels.persistence.ProgrammingGradingDao programmingGradingDao(
            judgels.hibernate.ProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static judgels.persistence.ProgrammingSubmissionDao programmingSubmissionDao(
            judgels.hibernate.ProgrammingSubmissionHibernateDao dao) {
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
    static judgels.persistence.TrainingProgrammingGradingDao trainingProgrammingGradingDao(
            judgels.hibernate.TrainingProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static judgels.persistence.TrainingProgrammingSubmissionDao trainingProgrammingSubmissionDao(
            judgels.hibernate.TrainingProgrammingSubmissionHibernateDao dao) {
        return dao;
    }
}
