package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.CLARIFICATIONS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;

import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestState;
import judgels.uriel.api.contest.web.ContestTab;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.role.RoleChecker;

public class ContestWebConfigFetcher {
    private final RoleChecker roleChecker;
    private final ContestAnnouncementDao announcementDao;
    private final ContestClarificationDao clarificationDao;
    private final Clock clock;

    @Inject
    public ContestWebConfigFetcher(
            RoleChecker roleChecker,
            ContestAnnouncementDao announcementDao,
            ContestClarificationDao clarificationDao,
            Clock clock) {

        this.roleChecker = roleChecker;
        this.announcementDao = announcementDao;
        this.clarificationDao = clarificationDao;
        this.clock = clock;
    }

    public ContestWebConfig fetchConfig(String userJid, Contest contest) {
        ImmutableSet.Builder<ContestTab> visibleTabs = ImmutableSet.builder();
        visibleTabs.add(ANNOUNCEMENTS);

        if (roleChecker.canViewProblems(userJid, contest)) {
            visibleTabs.add(PROBLEMS);
        }

        if (roleChecker.canViewOwnSubmissions(userJid, contest)) {
            visibleTabs.add(SUBMISSIONS);
        }

        if (roleChecker.canViewOwnClarifications(userJid, contest)) {
            visibleTabs.add(CLARIFICATIONS);
        }

        if (roleChecker.canViewDefaultScoreboard(userJid, contest)) {
            visibleTabs.add(SCOREBOARD);
        }

        // TODO(fushar): unit tests

        ContestState contestState;
        Optional<Duration> remainingContestStateDuration = Optional.empty();

        if (contest.hasFinished(clock)) {
            contestState = ContestState.ENDED;
        } else if (contest.isRunning(clock)) {
            contestState = ContestState.RUNNING;
            remainingContestStateDuration = Optional.of(Duration.between(clock.instant(), contest.getEndTime()));
        } else {
            contestState = ContestState.NOT_BEGUN;
            remainingContestStateDuration = Optional.of(Duration.between(clock.instant(), contest.getBeginTime()));
        }

        long announcementsCount = announcementDao.selectCountPublishedByContestJid(contest.getJid());
        long answeredClarificationsCount =
                clarificationDao.selectCountAnsweredByContestJidAndUserJid(contest.getJid(), userJid);

        return new ContestWebConfig.Builder()
                .visibleTabs(visibleTabs.build())
                .contestState(contestState)
                .remainingContestStateDuration(remainingContestStateDuration)
                .announcementsCount(announcementsCount)
                .answeredClarificationsCount(answeredClarificationsCount)
                .build();
    }
}
