package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIoiScoreboard.class)
public interface IoiScoreboard extends Scoreboard {
    IoiScoreboardContent getContent();

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

        List<IoiScoreboardEntry> filteredEntries = getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new IoiScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new IoiScoreboard.Builder()
                .state(filteredState)
                .content(new IoiScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }

    class Builder extends ImmutableIoiScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIoiScoreboardContent.class)
    interface IoiScoreboardContent {
        List<IoiScoreboardEntry> getEntries();

        class Builder extends ImmutableIoiScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIoiScoreboardEntry.class)
    interface IoiScoreboardEntry {
        int getRank();
        String getContestantJid();
        List<Optional<Integer>> getScores();
        int getTotalScores();
        long getLastAffectingPenalty();

        class Builder extends ImmutableIoiScoreboardEntry.Builder {}
    }
}
