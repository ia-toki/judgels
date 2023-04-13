package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class ChapterHibernateDao extends JudgelsHibernateDao<ChapterModel> implements ChapterDao {
    @Inject
    public ChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
