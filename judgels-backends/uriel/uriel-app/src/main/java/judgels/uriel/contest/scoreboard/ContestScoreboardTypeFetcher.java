package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import com.google.common.collect.ImmutableList;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
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

    public List<ContestScoreboardType> fetchViewableTypes(Contest contest, boolean canSuperviseScoreboard) {
        Optional<FrozenScoreboardModuleConfig> frozenScoreboardModuleConfig =
                moduleStore.getFrozenScoreboardModuleConfig(contest.getJid());
        if (frozenScoreboardModuleConfig.isPresent()) {
            if (frozenScoreboardModuleConfig.get().getIsOfficialScoreboardAllowed()) {
                return ImmutableList.of(OFFICIAL, FROZEN);
            }
            Instant freezeTime = contest.getEndTime().minus(
                    frozenScoreboardModuleConfig.get().getFreezeDurationBeforeEndTime());

            if (!clock.instant().isBefore(freezeTime)) {
                if (canSuperviseScoreboard) {
                    return ImmutableList.of(OFFICIAL, FROZEN);
                }
                return ImmutableList.of(FROZEN);
            }
        }

        return ImmutableList.of(OFFICIAL);
    }
}
