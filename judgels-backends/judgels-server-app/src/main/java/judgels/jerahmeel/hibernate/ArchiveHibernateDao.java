package judgels.jerahmeel.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.ArchiveModel;
import judgels.jerahmeel.persistence.ArchiveModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class ArchiveHibernateDao extends JudgelsHibernateDao<ArchiveModel> implements ArchiveDao {
    @Inject
    public ArchiveHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ArchiveModel> selectBySlug(String archiveSlug) {
        return select().where(columnEq(ArchiveModel_.slug, archiveSlug)).unique();
    }
}
