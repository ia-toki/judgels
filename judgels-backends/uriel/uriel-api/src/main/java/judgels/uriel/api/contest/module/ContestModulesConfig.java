package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestModulesConfig.class)
public interface ContestModulesConfig {
    Optional<TrocStyleModuleConfig> getTrocStyle();
    Optional<IcpcStyleModuleConfig> getIcpcStyle();
    Optional<IoiStyleModuleConfig> getIoiStyle();
    Optional<GcjStyleModuleConfig> getGcjStyle();
    Optional<BundleStyleModuleConfig> getBundleStyle();

    ScoreboardModuleConfig getScoreboard();

    Optional<ClarificationTimeLimitModuleConfig> getClarificationTimeLimit();
    Optional<DivisionModuleConfig> getDivision();
    Optional<ExternalScoreboardModuleConfig> getExternalScoreboard();
    Optional<FrozenScoreboardModuleConfig> getFrozenScoreboard();
    Optional<VirtualModuleConfig> getVirtual();

    class Builder extends ImmutableContestModulesConfig.Builder {}
}
