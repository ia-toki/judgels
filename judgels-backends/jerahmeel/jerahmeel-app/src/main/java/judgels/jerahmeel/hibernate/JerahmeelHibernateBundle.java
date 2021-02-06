package judgels.jerahmeel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jerahmeel.JerahmeelApplicationConfiguration;
import judgels.jerahmeel.persistence.ArchiveModel;
import judgels.jerahmeel.persistence.BundleItemSubmissionModel;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.CurriculumModel;
import judgels.jerahmeel.persistence.ProblemContestModel;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProgrammingGradingModel;
import judgels.jerahmeel.persistence.ProgrammingSubmissionModel;
import judgels.jerahmeel.persistence.StatsUserChapterModel;
import judgels.jerahmeel.persistence.StatsUserCourseModel;
import judgels.jerahmeel.persistence.StatsUserModel;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel;

public class JerahmeelHibernateBundle extends HibernateBundle<JerahmeelApplicationConfiguration> {
    public JerahmeelHibernateBundle() {
        super(
                ArchiveModel.class,
                CurriculumModel.class,
                ChapterModel.class,
                ChapterLessonModel.class,
                ChapterProblemModel.class,
                CourseModel.class,
                CourseChapterModel.class,
                ProgrammingGradingModel.class,
                ProgrammingSubmissionModel.class,
                BundleItemSubmissionModel.class,
                ProblemContestModel.class,
                ProblemSetModel.class,
                ProblemSetProblemModel.class,
                StatsUserModel.class,
                StatsUserChapterModel.class,
                StatsUserCourseModel.class,
                StatsUserProblemModel.class,
                StatsUserProblemSetModel.class
        );
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JerahmeelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
