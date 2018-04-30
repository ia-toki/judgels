package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFrozenScoreboardModuleConfig.class)
public interface FrozenScoreboardModuleConfig {
    boolean getIsOfficialScoreboardAllowed();
    Instant getScoreboardFreezeTime();

    class Builder extends ImmutableFrozenScoreboardModuleConfig.Builder {}
}
