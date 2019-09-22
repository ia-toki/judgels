package judgels.uriel.contest;

import com.google.common.collect.Ordering;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestTimer {
    private final ContestContestantStore contestantStore;
    private final ContestModuleStore moduleStore;
    private final Clock clock;

    @Inject
    public ContestTimer(ContestContestantStore contestantStore, ContestModuleStore moduleStore, Clock clock) {
        this.contestantStore = contestantStore;
        this.moduleStore = moduleStore;
        this.clock = clock;
    }

    public boolean isPaused(Contest contest) {
        return moduleStore.hasPausedModule(contest.getJid());
    }

    public Duration getDurationToBeginTime(Contest contest) {
        return Duration.between(clock.instant(), contest.getBeginTime());
    }

    public boolean hasBegun(Contest contest) {
        return !clock.instant().isBefore(contest.getBeginTime());
    }

    public Duration getDurationFromBeginTime(Contest contest) {
        return Duration.between(contest.getBeginTime(), clock.instant());
    }

    public boolean hasStarted(Contest contest, String userJid) {
        if (!hasBegun(contest)) {
            return false;
        }
        Optional<VirtualModuleConfig> config = moduleStore.getVirtualModuleConfig(contest.getJid());
        if (!config.isPresent()) {
            return true;
        }
        Optional<ContestContestant> contestant = contestantStore.getContestant(contest.getJid(), userJid);
        if (!contestant.isPresent()) {
            return true;
        }
        if (!contestant.get().getContestStartTime().isPresent()) {
            return false;
        }
        return !clock.instant().isBefore(contestant.get().getContestStartTime().get());
    }

    public Duration getDurationToFinishTime(Contest contest, String userJid) {
        Instant finishTime = Ordering.natural().min(contest.getEndTime(), getFinishTime(contest, userJid));
        return Duration.between(clock.instant(), finishTime);
    }

    public boolean hasFinished(Contest contest, String userJid) {
        return hasEnded(contest) || !clock.instant().isBefore(getFinishTime(contest, userJid));
    }

    public Duration getDurationToEndTime(Contest contest) {
        return Duration.between(clock.instant(), contest.getEndTime());
    }

    public boolean hasEnded(Contest contest) {
        return !clock.instant().isBefore(contest.getEndTime());
    }

    private Instant getFinishTime(Contest contest, String userJid) {
        Optional<VirtualModuleConfig> config = moduleStore.getVirtualModuleConfig(contest.getJid());
        if (!config.isPresent()) {
            return contest.getEndTime();
        }
        Optional<ContestContestant> contestant = contestantStore.getContestant(contest.getJid(), userJid);
        if (!contestant.isPresent() || !contestant.get().getContestStartTime().isPresent()) {
            return contest.getEndTime();
        }
        return contestant.get().getContestStartTime().get().plus(config.get().getVirtualDuration());
    }
}
