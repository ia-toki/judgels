package judgels.persistence;

import java.io.PrintWriter;
import java.util.Optional;

public interface ContestManagerDao extends Dao<ContestManagerModel> {
    QueryBuilder<ContestManagerModel> selectByContestJid(String contestJid);
    Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    void dump(PrintWriter output, String contestJid);
}
