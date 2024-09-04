package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;
import judgels.uriel.api.contest.module.ContestModuleType;

public interface ContestModuleDao extends Dao<ContestModuleModel> {
    ContestModuleQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type);
    Optional<ContestModuleModel> selectEnabledByContestJidAndType(String contestJid, ContestModuleType type);
    void dump(PrintWriter output, String contestJid);

    interface ContestModuleQueryBuilder extends QueryBuilder<ContestModuleModel> {
        ContestModuleQueryBuilder whereEnabled();
    }
}
