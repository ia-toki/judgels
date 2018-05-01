package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIcpcScoreboard.class)
public interface IcpcScoreboard extends Scoreboard {
    IcpcScoreboardContent getContent();

    @Override
    default Scoreboard filterContestantJids(Set<String> contestantJids) {
        Set<String> filteredContestantJids = getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<IcpcScoreboardEntry> filteredEntries = getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new IcpcScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new IcpcScoreboard.Builder()
                .state(filteredState)
                .content(new IcpcScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }

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
