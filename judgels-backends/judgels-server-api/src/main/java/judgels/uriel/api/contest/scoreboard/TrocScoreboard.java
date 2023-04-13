package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@JsonTypeName("TROC")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableTrocScoreboard.class)
@SuppressWarnings("immutables:from")
public interface TrocScoreboard extends Scoreboard {
    @Override
    TrocScoreboardContent getContent();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableTrocScoreboardContent.class)
    interface TrocScoreboardContent extends ScoreboardContent {
        @Override
        List<TrocScoreboardEntry> getEntries();

        class Builder extends ImmutableTrocScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableTrocScoreboardEntry.class)
    interface TrocScoreboardEntry extends ScoreboardEntry {
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
        List<TrocScoreboardProblemState> getProblemStateList();

        @JsonIgnore
        @Override
        default boolean hasSubmission() {
            return getAttemptsList().stream().anyMatch(a -> a > 0);
        }

        class Builder extends ImmutableTrocScoreboardEntry.Builder {}
    }

    enum TrocScoreboardProblemState {
        @JsonProperty NOT_ACCEPTED,
        @JsonProperty ACCEPTED,
        @JsonProperty FIRST_ACCEPTED,
        @JsonProperty FROZEN;

        @JsonValue
        int toValue() {
            return ordinal();
        }
    }

    class Builder extends ImmutableTrocScoreboard.Builder {}
}
