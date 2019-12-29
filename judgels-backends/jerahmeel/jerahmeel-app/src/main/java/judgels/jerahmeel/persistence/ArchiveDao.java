package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface ArchiveDao extends JudgelsDao<ArchiveModel> {
    Optional<ArchiveModel> selectBySlug(String archiveSlug);
}
