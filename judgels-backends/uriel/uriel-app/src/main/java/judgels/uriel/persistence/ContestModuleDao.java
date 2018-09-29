package judgels.uriel.persistence;

import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.uriel.api.contest.module.ContestModuleType;

public interface ContestModuleDao extends Dao<ContestModuleModel> {
    Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type);
    Optional<ContestModuleModel> selectEnabledByContestJidAndType(String contestJid, ContestModuleType type);
    Set<ContestModuleModel> selectAllEnabledByContestJid(String contestJid);
}
