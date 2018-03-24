package judgels.uriel.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;

public interface ContestDao extends JudgelsDao<ContestModel> {
    Page<ContestModel> selectAllByUserJid(String userJid, int page, int pageSize);
}
