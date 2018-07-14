package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIoiScoreboard.class)
public interface IoiScoreboard extends Scoreboard {
    IoiScoreboardContent getContent();

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
