package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIcpcScoreboard.class)
public interface IcpcScoreboard extends Scoreboard {
    State getState();
    Content getContent();

    class Builder extends ImmutableIcpcScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableState.class)
    interface State {
        List<String> getProblemJids();
        List<String> getProblemAliases();
        Set<String> getContestantJids();

        class Builder extends ImmutableState.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableContent.class)
    interface Content {
        List<Entry> getEntries();

        class Builder extends ImmutableContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEntry.class)
    interface Entry {
        int getRank();
        String getContestantJid();
        String getImageURL();
        int getTotalAccepted();
        int getTotalPenalties();
        int getLastAcceptedPenalty();
        List<Integer> getAttemptsList();
        List<Integer> getPenaltyList();
        List<ProblemState> getProblemStateList();

        class Builder extends ImmutableEntry.Builder {}
    }

    enum ProblemState {
        @JsonProperty NOT_ACCEPTED,
        @JsonProperty ACCEPTED,
        @JsonProperty FIRST_ACCEPTED;

        @JsonValue
        int toValue() {
            return ordinal();
        }
    }
}
