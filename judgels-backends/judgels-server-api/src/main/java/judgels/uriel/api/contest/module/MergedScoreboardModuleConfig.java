package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@JsonTypeName("MERGED_SCOREBOARD")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableMergedScoreboardModuleConfig.class)
public interface MergedScoreboardModuleConfig extends ModuleConfig {
    MergedScoreboardModuleConfig DEFAULT = new Builder()
            .previousContestJid("JIDCONT00000000000000000000")
            .build();

    Optional<String> getPreviousContestJid();

    class Builder extends ImmutableMergedScoreboardModuleConfig.Builder {}
}
