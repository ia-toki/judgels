package judgels.uriel.persistence;

import java.util.List;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ContestGroupContestDao extends Dao<ContestGroupContestModel> {
    List<ContestGroupContestModel> selectAllByContestGroupJid(String contestGroupJid, SelectionOptions options);
    Set<String> selectAllContestGroupJidsByContestJids(Set<String> contestJids);
}
