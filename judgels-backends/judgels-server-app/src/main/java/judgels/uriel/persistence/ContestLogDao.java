package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestLogDao extends UnmodifiableDao<ContestLogModel> {
    Page<ContestLogModel> selectPaged(
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            SelectionOptions options);
}
