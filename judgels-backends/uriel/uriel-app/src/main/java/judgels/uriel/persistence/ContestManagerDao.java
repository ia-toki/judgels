package judgels.uriel.persistence;

import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestManagerDao extends Dao<ContestManagerModel> {
    Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    Page<ContestManagerModel> selectPagedByContestJid(String contestJid, SelectionOptions options);
    Set<ContestManagerModel> selectAllByContestJid(String contestJid, SelectionOptions options);
}
