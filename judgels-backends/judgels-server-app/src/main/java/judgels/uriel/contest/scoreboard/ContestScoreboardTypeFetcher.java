package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestScoreboardTypeFetcher {
    private final ContestModuleStore moduleStore;
    private final Clock clock;

    @Inject
    public ContestScoreboardTypeFetcher(ContestModuleStore moduleStore, Clock clock) {
        this.moduleStore = moduleStore;
        this.clock = clock;
    }

    public ContestScoreboardType fetchDefaultType(Contest contest, boolean canSupervise) {
        if (moduleStore.getScoreboardModuleConfig(contest.getJid()).getIsIncognitoScoreboard()) {
            return OFFICIAL;
        }

        Optional<FrozenScoreboardModuleConfig> frozenScoreboardModuleConfig =
                moduleStore.getFrozenScoreboardModuleConfig(contest.getJid());
        if (frozenScoreboardModuleConfig.isPresent()) {
            if (frozenScoreboardModuleConfig.get().getIsOfficialScoreboardAllowed()) {
                return OFFICIAL;
            }
            Instant freezeTime = contest.getEndTime().minus(
                    frozenScoreboardModuleConfig.get().getFreezeDurationBeforeEndTime());

            if (!clock.instant().isBefore(freezeTime)) {
                return canSupervise ? OFFICIAL : FROZEN;
            }
        }

        return OFFICIAL;
    }
}
