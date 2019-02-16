package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;

public interface ScoreboardProcessor {
    Scoreboard parseFromString(ObjectMapper mapper, String json);
    String computeToString(
            ObjectMapper mapper,
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> submissions,
            Optional<Instant> freezeTime);

    int getTotalEntries(Scoreboard scoreboard);
    List<?> getEntries(Scoreboard scoreboard);
    Scoreboard replaceEntries(Scoreboard scoreboard, List<?> entries);
    Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids);

    default Scoreboard paginate(Scoreboard scoreboard, int page, int pageSize) {
        List<? extends List<?>> partition = Lists.partition(getEntries(scoreboard), pageSize);

        List<?> partitionPage;
        if (page <= partition.size()) {
            partitionPage = partition.get(page - 1);
        } else {
            partitionPage = new ArrayList();
        }

        return replaceEntries(scoreboard, partitionPage);
    }
}
