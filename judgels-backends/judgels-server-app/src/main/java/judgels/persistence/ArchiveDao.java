package judgels.persistence;

import java.util.Optional;

public interface ArchiveDao extends JudgelsDao<ArchiveModel> {
    Optional<ArchiveModel> selectBySlug(String archiveSlug);
}
