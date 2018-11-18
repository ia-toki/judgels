package judgels.uriel.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManager.class)
public interface ContestManager {
    String getUserJid();

    class Builder extends ImmutableContestManager.Builder {}
}
