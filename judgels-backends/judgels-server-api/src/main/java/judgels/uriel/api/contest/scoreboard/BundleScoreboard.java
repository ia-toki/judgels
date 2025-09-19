package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@JsonTypeName("BUNDLE")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableBundleScoreboard.class)
@SuppressWarnings("immutables:from")
public interface BundleScoreboard extends Scoreboard {
    @Override
    BundleScoreboardContent getContent();

    class Builder extends ImmutableBundleScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableBundleScoreboardContent.class)
    interface BundleScoreboardContent extends ScoreboardContent {
        @Override
        List<BundleScoreboardEntry> getEntries();

        class Builder extends ImmutableBundleScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableBundleScoreboardEntry.class)
    interface BundleScoreboardEntry extends ScoreboardEntry {
        int getTotalScores();
        List<Integer> getScores();
        Optional<Instant> getLastAnsweredTime();

        @JsonIgnore
        @Override
        default boolean hasSubmission() {
            return getLastAnsweredTime().isPresent();
        }

        class Builder extends ImmutableBundleScoreboardEntry.Builder {}
    }
}
