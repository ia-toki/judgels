package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.immutables.value.Value;

@JsonTypeName("FROZEN_SCOREBOARD")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableFrozenScoreboardModuleConfig.class)
public interface FrozenScoreboardModuleConfig extends ModuleConfig {
    FrozenScoreboardModuleConfig DEFAULT = new Builder()
            .isOfficialScoreboardAllowed(false)
            .freezeDurationBeforeEndTime(Duration.ofHours(1))
            .build();

    boolean getIsOfficialScoreboardAllowed();

    @JsonProperty("scoreboardFreezeTime")
    Duration getFreezeDurationBeforeEndTime();

    class Builder extends ImmutableFrozenScoreboardModuleConfig.Builder {}
}
