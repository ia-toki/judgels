package judgels.contest.scoreboard;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.api.contest.contestant.ContestContestant;
import judgels.api.contest.module.StyleModuleConfig;
import judgels.api.contest.scoreboard.ScoreboardState;
import judgels.api.profile.Profile;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.api.submission.programming.Submission;
import judgels.grading.api.ScoringConfig;
import org.immutables.value.Value;

@Value.Immutable
public interface ScoreboardProcessRequest {
    ScoreboardState getScoreboardState();
    Optional<ScoreboardIncrementalContent> getIncrementalContent();
    StyleModuleConfig getStyleModuleConfig();
    Map<String, Set<ContestContestant>> getContestContestantsMap();
    Map<String, Instant> getContestBeginTimesMap();
    Map<String, Instant> getContestFreezeTimesMap();
    Map<String, ScoringConfig> getProblemScoringConfigsMap();
    Map<String, Profile> getProfilesMap();
    List<Submission> getProgrammingSubmissions();
    List<ItemSubmission> getBundleItemSubmissions();

    // Contestants who are shown on the scoreboard but excluded from rank numbering (rank 0).
    Set<String> getUnofficialContestantJids();

    class Builder extends ImmutableScoreboardProcessRequest.Builder {}
}
