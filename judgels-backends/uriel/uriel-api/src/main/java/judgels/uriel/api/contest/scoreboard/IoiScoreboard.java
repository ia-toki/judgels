package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@JsonTypeName("IOI")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableIoiScoreboard.class)
@SuppressWarnings("immutables:from")
public interface IoiScoreboard extends Scoreboard {
    @Override
    IoiScoreboardContent getContent();

    class Builder extends ImmutableIoiScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIoiScoreboardContent.class)
    interface IoiScoreboardContent extends ScoreboardContent {
        @Override
        List<IoiScoreboardEntry> getEntries();

        class Builder extends ImmutableIoiScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableIoiScoreboardEntry.class)
    interface IoiScoreboardEntry extends ScoreboardEntry {
        List<Optional<Integer>> getScores();
        int getTotalScores();
        long getLastAffectingPenalty();

        class Builder extends ImmutableIoiScoreboardEntry.Builder {}
    }
}
