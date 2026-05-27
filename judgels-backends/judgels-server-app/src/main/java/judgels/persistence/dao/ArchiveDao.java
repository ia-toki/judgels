package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.ArchiveModel;

public interface ArchiveDao extends JudgelsDao<ArchiveModel> {
    Optional<ArchiveModel> selectBySlug(String archiveSlug);
}
