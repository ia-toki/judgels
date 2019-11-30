package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ProblemSetProblemDao extends Dao<ProblemSetProblemModel> {
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemJid(String problemSetJid, String problemJid);
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemAlias(String problemSetJid, String problemAlias);
    List<ProblemSetProblemModel> selectAllByProblemSetJid(String problemSetJid, SelectionOptions options);
}
