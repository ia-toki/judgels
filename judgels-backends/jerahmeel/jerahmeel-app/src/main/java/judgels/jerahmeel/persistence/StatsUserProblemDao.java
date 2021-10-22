package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface StatsUserProblemDao extends Dao<StatsUserProblemModel> {
    Optional<StatsUserProblemModel> selectByUserJidAndProblemJid(String userJid, String problemJid);
    List<StatsUserProblemModel> selectAllByUserJidAndProblemJids(String userJid, Set<String> problemJids);
    List<StatsUserProblemModel> selectAllByUserJidsAndProblemJids(Set<String> userJids, Set<String> problemJids);
    List<StatsUserProblemModel> selectAllAcceptedByProblemJid(String problemJid, SelectionOptions options);
    List<StatsUserProblemModel> selectAllByProblemJid(String problemJid, SelectionOptions options);
    Map<String, Long> selectTotalScoresByProblemJids(Set<String> problemJids);
    Map<String, Long> selectCountsAcceptedByProblemJids(Set<String> problemJids);
    Map<String, Long> selectCountsTriedByProblemJids(Set<String> problemJids);
    long selectCountTriedByUserJid(String userJid);
    Map<String, Long> selectCountsVerdictByUserJid(String userJid);
}
