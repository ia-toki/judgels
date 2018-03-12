package judgels.uriel.contest;

import judgels.persistence.api.Page;

public interface ContestRawDao {
    Page<ContestModel> selectAllByUserJid(String userJid, int page, int pageSize);
}
