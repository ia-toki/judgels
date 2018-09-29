package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableScoreboardModuleConfig.class)
public interface ScoreboardModuleConfig {
    ScoreboardModuleConfig DEFAULT = new Builder()
            .isIncognitoScoreboard(false)
            .build();

    boolean getIsIncognitoScoreboard();

    class Builder extends ImmutableScoreboardModuleConfig.Builder {}
}
