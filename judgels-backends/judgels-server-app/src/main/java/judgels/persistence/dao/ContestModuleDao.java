package judgels.persistence.dao;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.api.contest.module.ContestModuleType;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ContestModuleModel;

public interface ContestModuleDao extends Dao<ContestModuleModel> {
    ContestModuleQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type);
    Optional<ContestModuleModel> selectEnabledByContestJidAndType(String contestJid, ContestModuleType type);
    void dump(PrintWriter output, String contestJid);

    interface ContestModuleQueryBuilder extends QueryBuilder<ContestModuleModel> {
        ContestModuleQueryBuilder whereEnabled();
    }
}
