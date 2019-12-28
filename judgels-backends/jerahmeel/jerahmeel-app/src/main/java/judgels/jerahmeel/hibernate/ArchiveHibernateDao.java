package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.ArchiveModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class ArchiveHibernateDao extends JudgelsHibernateDao<ArchiveModel> implements ArchiveDao {
    @Inject
    public ArchiveHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
