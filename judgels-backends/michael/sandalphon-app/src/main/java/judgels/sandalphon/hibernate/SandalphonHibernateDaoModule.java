package judgels.sandalphon.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonPartnerDao;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemSetterDao;
import judgels.sandalphon.persistence.ProblemTagDao;
import judgels.sandalphon.persistence.ProgrammingGradingDao;
import judgels.sandalphon.persistence.ProgrammingSubmissionDao;

@Module
public class SandalphonHibernateDaoModule {
    private SandalphonHibernateDaoModule() {}

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
}
