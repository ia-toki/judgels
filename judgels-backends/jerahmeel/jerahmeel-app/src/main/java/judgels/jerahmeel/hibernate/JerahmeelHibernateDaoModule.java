package judgels.jerahmeel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.jerahmeel.persistence.AdminRoleDao;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CurriculumDao;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;

@Module
public class JerahmeelHibernateDaoModule {
    private JerahmeelHibernateDaoModule() {}

    @Provides
    static AdminRoleDao adminRoleDao(AdminRoleHibernateDao dao) {
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
    static ProblemSetDao problemSetDao(ProblemSetHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProblemSetProblemDao problemSetProblemDao(ProblemSetProblemHibernateDao dao) {
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
    static BundleItemSubmissionDao bundleItemSubmissionDao(BundleItemSubmissionHibernateDao dao) {
        return dao;
    }
}
