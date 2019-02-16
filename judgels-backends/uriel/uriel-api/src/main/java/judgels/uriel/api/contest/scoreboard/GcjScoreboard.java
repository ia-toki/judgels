package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGcjScoreboard.class)
public interface GcjScoreboard extends Scoreboard {
    GcjScoreboardContent getContent();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableGcjScoreboardContent.class)
    interface GcjScoreboardContent {
        List<GcjScoreboardEntry> getEntries();

        class Builder extends ImmutableGcjScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableGcjScoreboardEntry.class)
    interface GcjScoreboardEntry {
        int getRank();
        String getContestantJid();
        int getTotalPoints();
        long getTotalPenalties();
        List<Integer> getAttemptsList();
        List<Long> getPenaltyList();
        List<GcjScoreboardProblemState> getProblemStateList();

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
