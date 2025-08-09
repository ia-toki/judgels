package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestManagerDao extends Dao<ContestManagerModel> {
    QueryBuilder<ContestManagerModel> selectByContestJid(String contestJid);
    Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    void dump(PrintWriter output, String contestJid);
}
