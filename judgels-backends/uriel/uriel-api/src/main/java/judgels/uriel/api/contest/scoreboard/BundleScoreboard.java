package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleScoreboard.class)
public interface BundleScoreboard extends Scoreboard {
    BundleScoreboardContent getContent();

    class Builder extends ImmutableBundleScoreboard.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableBundleScoreboardContent.class)
    interface BundleScoreboardContent {
        List<BundleScoreboardEntry> getEntries();

        class Builder extends ImmutableBundleScoreboardContent.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableBundleScoreboardEntry.class)
    interface BundleScoreboardEntry {
        int getRank();
        String getContestantJid();
        List<Integer> getAnsweredItems();
        int getTotalAnsweredItems();
        Optional<Instant> getLastAnsweredTime();

        class Builder extends ImmutableBundleScoreboardEntry.Builder {}
    }
}
