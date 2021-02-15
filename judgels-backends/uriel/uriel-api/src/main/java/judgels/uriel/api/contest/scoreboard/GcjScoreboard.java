package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@JsonTypeName("GCJ")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableGcjScoreboard.class)
@SuppressWarnings("immutables:from")
public interface GcjScoreboard extends Scoreboard {
    @Override
    GcjScoreboardContent getContent();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableGcjScoreboardContent.class)
    interface GcjScoreboardContent extends ScoreboardContent {
        @Override
        List<GcjScoreboardEntry> getEntries();

        class Builder extends ImmutableGcjScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableGcjScoreboardEntry.class)
    interface GcjScoreboardEntry extends ScoreboardEntry {
        @Value.Default
        default String getContestantUsername() {
            return "";
        }

        @Value.Default
        default int getContestantRating() {
            return 0;
        }

        int getTotalPoints();
        long getTotalPenalties();
        List<Integer> getAttemptsList();
        List<Long> getPenaltyList();
        List<GcjScoreboardProblemState> getProblemStateList();

        @JsonIgnore
        @Override
        default boolean hasSubmission() {
            return getAttemptsList().stream().anyMatch(a -> a > 0);
        }

        class Builder extends ImmutableGcjScoreboardEntry.Builder {}
    }

    enum GcjScoreboardProblemState {
        @JsonProperty NOT_ACCEPTED,
        @JsonProperty ACCEPTED,
        @JsonProperty FROZEN;

        @JsonValue
        int toValue() {
            return ordinal();
        }
    }

    class Builder extends ImmutableGcjScoreboard.Builder {}
}
