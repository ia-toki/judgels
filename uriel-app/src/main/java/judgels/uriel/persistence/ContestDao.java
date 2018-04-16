package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestDao extends JudgelsDao<ContestModel> {
    Page<ContestModel> selectAllByUserJid(Optional<String> userJid, SelectionOptions options);
    List<ContestModel> selectAllActiveByUserJid(Optional<String> userJid, SelectionOptions options);
    Page<ContestModel> selectAllPastByUserJid(Optional<String> userJid, SelectionOptions options);
}
