package judgels.uriel.contest;

import judgels.persistence.api.Page;
import judgels.uriel.persistence.ContestModel;

public interface ContestRawDao {
    Page<ContestModel> selectAllByUserJid(String userJid, int page, int pageSize);
}
