package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.uriel.api.contest.module.ContestModuleType;

public interface ContestModuleDao extends Dao<ContestModuleModel> {
    Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type);
}
