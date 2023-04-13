package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@JsonTypeName("ICPC")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableIcpcScoreboard.class)
@SuppressWarnings("immutables:from")
public interface IcpcScoreboard extends Scoreboard {
    @Override
    IcpcScoreboardContent getContent();

    class Builder extends ImmutableIcpcScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIcpcScoreboardContent.class)
    interface IcpcScoreboardContent extends ScoreboardContent {
        @Override
        List<IcpcScoreboardEntry> getEntries();

        class Builder extends ImmutableIcpcScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIcpcScoreboardEntry.class)
    interface IcpcScoreboardEntry extends ScoreboardEntry {
        @Value.Default
        default String getContestantUsername() {
            return "";
        }

        @Value.Default
        default int getContestantRating() {
            return 0;
        }

        int getTotalAccepted();
        long getTotalPenalties();
        long getLastAcceptedPenalty();
        List<Integer> getAttemptsList();
        List<Long> getPenaltyList();
        List<IcpcScoreboardProblemState> getProblemStateList();

        @JsonIgnore
        @Override
        default boolean hasSubmission() {
            return getAttemptsList().stream().anyMatch(a -> a > 0);
        }

        class Builder extends ImmutableIcpcScoreboardEntry.Builder {}
    }

    enum IcpcScoreboardProblemState {
        @JsonProperty NOT_ACCEPTED,
        @JsonProperty ACCEPTED,
        @JsonProperty FIRST_ACCEPTED,
        @JsonProperty FROZEN;

        @JsonValue
        int toValue() {
            return ordinal();
        }
    }
}
