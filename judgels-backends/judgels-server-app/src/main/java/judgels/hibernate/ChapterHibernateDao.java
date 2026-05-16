package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.ChapterDao;
import judgels.persistence.ChapterModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class ChapterHibernateDao extends JudgelsHibernateDao<ChapterModel> implements ChapterDao {
    @Inject
    public ChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
