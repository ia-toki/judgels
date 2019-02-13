package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.sandalphon.api.submission.ProgrammingSubmission;
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
            List<ProgrammingSubmission> submissions,
            Optional<Instant> freezeTime);
    Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids);
}
