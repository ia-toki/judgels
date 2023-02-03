package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestModulesConfig.class)
public interface ContestModulesConfig {
    Optional<TrocStyleModuleConfig> getTrocStyle();
    Optional<IcpcStyleModuleConfig> getIcpcStyle();
    Optional<IoiStyleModuleConfig> getIoiStyle();
    Optional<GcjStyleModuleConfig> getGcjStyle();
    Optional<BundleStyleModuleConfig> getBundleStyle();

    @Value.Default
    default ScoreboardModuleConfig getScoreboard() {
        return ScoreboardModuleConfig.DEFAULT;
    }

    Optional<ClarificationTimeLimitModuleConfig> getClarificationTimeLimit();
    Optional<DivisionModuleConfig> getDivision();
    Optional<EditorialModuleConfig> getEditorial();
    Optional<ExternalScoreboardModuleConfig> getExternalScoreboard();
    Optional<FrozenScoreboardModuleConfig> getFrozenScoreboard();
    Optional<MergedScoreboardModuleConfig> getMergedScoreboard();
    Optional<VirtualModuleConfig> getVirtual();

    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestModulesConfig.Builder {}
}
