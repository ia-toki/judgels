package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;

public interface ScoreboardProcessor {
    Scoreboard parse(ObjectMapper mapper, String json);
    Scoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries);

    boolean requiresGradingDetails(StyleModuleConfig styleModuleConfig);

    ScoreboardProcessResult process(
            Contest contest,
            ScoreboardState scoreboardState,
            Optional<ScoreboardIncrementalContent> incrementalContent,
            StyleModuleConfig styleModuleConfig,
            Set<ContestContestant> contestants,
            Map<String, Profile> profilesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime);

    ScoreboardEntry clearEntryRank(ScoreboardEntry entry);
}
