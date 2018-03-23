package judgels.uriel.persistence;

import judgels.persistence.api.Page;

public interface ContestRawDao {
    Page<ContestModel> selectAllByUserJid(String userJid, int page, int pageSize);
}
