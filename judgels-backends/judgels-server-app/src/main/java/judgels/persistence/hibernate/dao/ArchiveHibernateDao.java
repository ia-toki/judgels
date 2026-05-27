package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.ArchiveDao;
import judgels.persistence.model.ArchiveModel;
import judgels.persistence.model.ArchiveModel_;

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
