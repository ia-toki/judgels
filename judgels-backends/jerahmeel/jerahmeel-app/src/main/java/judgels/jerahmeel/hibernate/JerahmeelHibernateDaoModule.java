package judgels.jerahmeel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.jerahmeel.persistence.AdminRoleDao;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseDao;
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
    static ProgrammingGradingDao programmingGradingDao(ProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static ProgrammingSubmissionDao programmingSubmissionDao(ProgrammingSubmissionHibernateDao dao) {
        return dao;
    }
}
