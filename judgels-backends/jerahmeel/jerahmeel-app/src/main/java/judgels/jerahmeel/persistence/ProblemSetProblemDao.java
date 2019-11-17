package judgels.jerahmeel.persistence;

import java.util.List;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ProblemSetProblemDao extends Dao<ProblemSetProblemModel> {
    List<ProblemSetProblemModel> selectAllByProblemSetJid(String problemSetJid, SelectionOptions options);
}
