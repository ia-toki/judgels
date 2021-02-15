package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        @Value.Default
        default String getContestantUsername() {
            return "";
        }

        @Value.Default
        default int getContestantRating() {
            return 0;
        }

        int getTotalScores();
        long getLastAffectingPenalty();
        List<Optional<Integer>> getScores();

        @JsonIgnore
        @Override
        default boolean hasSubmission() {
            return getScores().stream().anyMatch(Optional::isPresent);
        }

        class Builder extends ImmutableIoiScoreboardEntry.Builder {}
    }
}
