package judgels.jerahmeel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface StatsUserProblemDao extends Dao<StatsUserProblemModel> {
    Optional<StatsUserProblemModel> selectByUserJidAndProblemJid(String userJid, String problemJid);
    List<StatsUserProblemModel> selectAllByUserJidAndProblemJids(String userJid, Collection<String> problemJids);
    List<StatsUserProblemModel> selectAllByUserJidsAndProblemJids(Collection<String> userJids, Collection<String> problemJids);
    QueryBuilder<StatsUserProblemModel> selectAcceptedByProblemJid(String problemJid);
    QueryBuilder<StatsUserProblemModel> selectByProblemJid(String problemJid);
    Map<String, Long> selectTotalScoresByProblemJids(Collection<String> problemJids);
    Map<String, Long> selectCountsAcceptedByProblemJids(Collection<String> problemJids);
    Map<String, Long> selectCountsTriedByProblemJids(Collection<String> problemJids);
    long selectCountTriedByUserJid(String userJid);
    int selectTotalScoreByUserJid(String userJid);
    Map<String, Long> selectCountsVerdictByUserJid(String userJid);
    void deleteAllByProblemJid(String problemJid);
}
