package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIcpcScoreboard.class)
public interface IcpcScoreboard extends Scoreboard {
    IcpcScoreboardContent getContent();

    class Builder extends ImmutableIcpcScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIcpcScoreboardContent.class)
    interface IcpcScoreboardContent {
        List<IcpcScoreboardEntry> getEntries();

        class Builder extends ImmutableIcpcScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIcpcScoreboardEntry.class)
    interface IcpcScoreboardEntry {
        int getRank();
        String getContestantJid();
        int getTotalAccepted();
        int getTotalPenalties();
        int getLastAcceptedPenalty();
        List<Integer> getAttemptsList();
        List<Long> getPenaltyList();
        List<IcpcScoreboardProblemState> getProblemStateList();

        class Builder extends ImmutableIcpcScoreboardEntry.Builder {}
    }

    enum IcpcScoreboardProblemState {
        @JsonProperty NOT_ACCEPTED,
        @JsonProperty ACCEPTED,
        @JsonProperty FIRST_ACCEPTED;

        @JsonValue
        int toValue() {
            return ordinal();
        }
    }
}
