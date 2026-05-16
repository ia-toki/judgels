package judgels.service.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.contrib.hibernate.UserRegistrationEmailHibernateDao;
import judgels.contrib.persistence.UserRegistrationEmailDao;
import judgels.jerahmeel.hibernate.ArchiveHibernateDao;
import judgels.jerahmeel.hibernate.BundleItemSubmissionHibernateDao;
import judgels.jerahmeel.hibernate.ChapterHibernateDao;
import judgels.jerahmeel.hibernate.ChapterLessonHibernateDao;
import judgels.jerahmeel.hibernate.ChapterProblemHibernateDao;
import judgels.jerahmeel.hibernate.CourseChapterHibernateDao;
import judgels.jerahmeel.hibernate.CourseHibernateDao;
import judgels.jerahmeel.hibernate.CurriculumHibernateDao;
import judgels.jerahmeel.hibernate.ProblemContestHibernateDao;
import judgels.jerahmeel.hibernate.ProblemLevelHibernateDao;
import judgels.jerahmeel.hibernate.ProblemSetHibernateDao;
import judgels.jerahmeel.hibernate.ProblemSetProblemHibernateDao;
import judgels.jerahmeel.hibernate.StatsUserHibernateDao;
import judgels.jerahmeel.hibernate.StatsUserProblemHibernateDao;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CurriculumDao;
import judgels.jerahmeel.persistence.ProblemContestDao;
import judgels.jerahmeel.persistence.ProblemLevelDao;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jophiel.hibernate.SessionHibernateDao;
import judgels.jophiel.hibernate.UserHibernateDao;
import judgels.jophiel.hibernate.UserInfoHibernateDao;
import judgels.jophiel.hibernate.UserRatingEventHibernateDao;
import judgels.jophiel.hibernate.UserRatingHibernateDao;
import judgels.jophiel.hibernate.UserResetPasswordHibernateDao;
import judgels.jophiel.hibernate.UserRoleHibernateDao;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserInfoDao;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserResetPasswordDao;
import judgels.jophiel.persistence.UserRoleDao;
import judgels.sandalphon.hibernate.BundleGradingHibernateDao;
import judgels.sandalphon.hibernate.BundleSubmissionHibernateDao;
import judgels.sandalphon.hibernate.LessonHibernateDao;
import judgels.sandalphon.hibernate.LessonPartnerHibernateDao;
import judgels.sandalphon.hibernate.ProblemHibernateDao;
import judgels.sandalphon.hibernate.ProblemPartnerHibernateDao;
import judgels.sandalphon.hibernate.ProblemSetterHibernateDao;
import judgels.sandalphon.hibernate.ProblemTagHibernateDao;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonPartnerDao;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemSetterDao;
import judgels.sandalphon.persistence.ProblemTagDao;
import judgels.uriel.hibernate.ContestAnnouncementHibernateDao;
import judgels.uriel.hibernate.ContestBundleItemSubmissionHibernateDao;
import judgels.uriel.hibernate.ContestClarificationHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.hibernate.ContestHibernateDao;
import judgels.uriel.hibernate.ContestLogHibernateDao;
import judgels.uriel.hibernate.ContestManagerHibernateDao;
import judgels.uriel.hibernate.ContestModuleHibernateDao;
import judgels.uriel.hibernate.ContestProblemHibernateDao;
import judgels.uriel.hibernate.ContestProgrammingGradingHibernateDao;
import judgels.uriel.hibernate.ContestProgrammingSubmissionHibernateDao;
import judgels.uriel.hibernate.ContestRoleHibernateDao;
import judgels.uriel.hibernate.ContestScoreboardHibernateDao;
import judgels.uriel.hibernate.ContestStyleHibernateDao;
import judgels.uriel.hibernate.ContestSupervisorHibernateDao;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestSupervisorDao;

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
    static judgels.sandalphon.persistence.ProgrammingGradingDao sandalphonProgrammingGradingDao(
            judgels.sandalphon.hibernate.ProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static judgels.sandalphon.persistence.ProgrammingSubmissionDao sandalphonProgrammingSubmissionDao(
            judgels.sandalphon.hibernate.ProgrammingSubmissionHibernateDao dao) {
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
    static judgels.jerahmeel.persistence.ProgrammingGradingDao jerahmeelProgrammingGradingDao(
            judgels.jerahmeel.hibernate.ProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static judgels.jerahmeel.persistence.ProgrammingSubmissionDao jerahmeelProgrammingSubmissionDao(
            judgels.jerahmeel.hibernate.ProgrammingSubmissionHibernateDao dao) {
        return dao;
    }
}
