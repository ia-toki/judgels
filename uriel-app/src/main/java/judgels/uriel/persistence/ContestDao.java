package judgels.uriel.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestDao extends JudgelsDao<ContestModel> {
    List<ContestModel> selectAllActive(SelectionOptions options);
    Page<ContestModel> selectPagedPast(SelectionOptions options);

    Page<ContestModel> selectPagedByUserJid(String userJid, SelectionOptions options);
    List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options);
    Page<ContestModel> selectPagedPastByUserJid(String userJid, SelectionOptions options);
}
