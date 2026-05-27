package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.ChapterDao;
import judgels.persistence.model.ChapterModel;

public class ChapterHibernateDao extends JudgelsHibernateDao<ChapterModel> implements ChapterDao {
    @Inject
    public ChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
