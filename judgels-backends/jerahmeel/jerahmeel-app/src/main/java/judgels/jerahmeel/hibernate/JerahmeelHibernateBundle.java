package judgels.jerahmeel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jerahmeel.JerahmeelApplicationConfiguration;
import judgels.jerahmeel.persistence.AdminRoleModel;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseModel;

public class JerahmeelHibernateBundle extends HibernateBundle<JerahmeelApplicationConfiguration> {
    public JerahmeelHibernateBundle() {
        super(
                AdminRoleModel.class,
                ChapterModel.class,
                ChapterLessonModel.class,
                CourseModel.class,
                CourseChapterModel.class
        );
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JerahmeelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
