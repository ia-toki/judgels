package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboard.class)
public interface ContestScoreboard {
    String getContestJid();
    String getScoreboard();
    boolean isOfficial();

    class Builder extends ImmutableContestScoreboard.Builder {}
}
