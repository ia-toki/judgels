package judgels.uriel.persistence;

import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestSupervisorDao extends Dao<ContestSupervisorModel> {
    Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    Page<ContestSupervisorModel> selectPagedByContestJid(String contestJid, SelectionOptions options);
    Set<ContestSupervisorModel> selectAllByContestJid(String contestJid, SelectionOptions options);
}
