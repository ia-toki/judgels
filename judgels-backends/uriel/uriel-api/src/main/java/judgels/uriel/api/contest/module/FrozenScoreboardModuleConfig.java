package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFrozenScoreboardModuleConfig.class)
public interface FrozenScoreboardModuleConfig {
    FrozenScoreboardModuleConfig DEFAULT = new Builder()
            .isOfficialScoreboardAllowed(false)
            .scoreboardFreezeTime(Instant.ofEpochSecond(0))
            .build();

    boolean getIsOfficialScoreboardAllowed();
    Instant getScoreboardFreezeTime();

    class Builder extends ImmutableFrozenScoreboardModuleConfig.Builder {}
}
