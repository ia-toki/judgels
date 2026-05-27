package judgels.persistence.dao;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ContestManagerModel;

public interface ContestManagerDao extends Dao<ContestManagerModel> {
    QueryBuilder<ContestManagerModel> selectByContestJid(String contestJid);
    Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    void dump(PrintWriter output, String contestJid);
}
