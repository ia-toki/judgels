package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestDao extends JudgelsDao<ContestModel> {
    Optional<ContestModel> selectBySlug(String contestSlug);

    List<ContestModel> selectAllActive(SelectionOptions options);

    Page<ContestModel> selectPagedByUserJid(String userJid, SelectionOptions options);
    List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options);
}
