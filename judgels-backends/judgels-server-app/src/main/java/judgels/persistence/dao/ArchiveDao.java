package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.ArchiveModel;
import judgels.persistence.model.ArchiveModel_;

public class ArchiveDao extends JudgelsDao<ArchiveModel> {
    @Inject
    public ArchiveDao(DaoData data) {
        super(data);
    }

    public Optional<ArchiveModel> selectBySlug(String archiveSlug) {
        return select().where(columnEq(ArchiveModel_.slug, archiveSlug)).unique();
    }
}
