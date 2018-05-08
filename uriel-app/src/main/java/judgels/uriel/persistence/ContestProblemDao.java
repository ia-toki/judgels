package judgels.uriel.persistence;

import java.util.List;
import judgels.persistence.Dao;

public interface ContestProblemDao extends Dao<ContestProblemModel> {
    List<ContestProblemModel> selectAllByContestJid(String contestJid);
    List<ContestProblemModel> selectAllOpenByContestJid(String contestJid);
}
