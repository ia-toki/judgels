package judgels.uriel.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestDao extends JudgelsDao<ContestModel> {
    List<ContestModel> selectAllActive(SelectionOptions options);
    Page<ContestModel> selectAllPast(SelectionOptions options);

    Page<ContestModel> selectAllByUserJid(String userJid, SelectionOptions options);
    List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options);
    Page<ContestModel> selectAllPastByUserJid(String userJid, SelectionOptions options);
}
