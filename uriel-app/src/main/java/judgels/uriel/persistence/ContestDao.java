package judgels.uriel.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestDao extends JudgelsDao<ContestModel> {
    Page<ContestModel> selectAllByUserJid(String userJid, SelectionOptions options);
}
