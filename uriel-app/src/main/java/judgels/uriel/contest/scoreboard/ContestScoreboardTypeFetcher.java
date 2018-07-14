package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import com.google.common.collect.ImmutableList;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
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

    public List<ContestScoreboardType> fetchViewableTypes(String contestJid, boolean canSuperviseScoreboard) {
        Optional<FrozenScoreboardModuleConfig> frozenScoreboardModuleConfig =
                moduleStore.getFrozenScoreboardModuleConfig(contestJid);
        if (frozenScoreboardModuleConfig.isPresent()) {
            if (frozenScoreboardModuleConfig.get().getIsOfficialScoreboardAllowed()) {
                return ImmutableList.of(OFFICIAL, FROZEN);
            }
            if (!clock.instant().isBefore(frozenScoreboardModuleConfig.get().getScoreboardFreezeTime())) {
                if (canSuperviseScoreboard) {
                    return ImmutableList.of(OFFICIAL, FROZEN);
                }
                return ImmutableList.of(FROZEN);
            }
        }

        return ImmutableList.of(OFFICIAL);
    }
}
