package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.CLARIFICATIONS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestState;
import judgels.uriel.api.contest.web.ContestTab;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.clarification.ContestClarificationRoleChecker;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.scoreboard.ContestScoreboardRoleChecker;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;

public class ContestWebConfigFetcher {
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestClarificationRoleChecker clarificationRoleChecker;
    private final ContestScoreboardRoleChecker scoreboardRoleChecker;
    private final ContestAnnouncementDao announcementDao;
    private final ContestClarificationDao clarificationDao;
    private final ContestTimer contestTimer;

    @Inject
    public ContestWebConfigFetcher(
            ContestProblemRoleChecker problemRoleChecker,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestClarificationRoleChecker clarificationRoleChecker,
            ContestScoreboardRoleChecker scoreboardRoleChecker,
            ContestAnnouncementDao announcementDao,
            ContestClarificationDao clarificationDao,
            ContestTimer contestTimer) {

        this.problemRoleChecker = problemRoleChecker;
        this.submissionRoleChecker = submissionRoleChecker;
        this.clarificationRoleChecker = clarificationRoleChecker;
        this.scoreboardRoleChecker = scoreboardRoleChecker;
        this.announcementDao = announcementDao;
        this.clarificationDao = clarificationDao;
        this.contestTimer = contestTimer;
    }

    public ContestWebConfig fetchConfig(String userJid, Contest contest) {
        ImmutableSet.Builder<ContestTab> visibleTabs = ImmutableSet.builder();
        visibleTabs.add(ANNOUNCEMENTS);

        if (problemRoleChecker.canViewProblems(userJid, contest)) {
            visibleTabs.add(PROBLEMS);
        }

        if (submissionRoleChecker.canViewOwnSubmissions(userJid, contest)) {
            visibleTabs.add(SUBMISSIONS);
        }

        if (clarificationRoleChecker.canViewOwnClarifications(userJid, contest)) {
            visibleTabs.add(CLARIFICATIONS);
        }

        if (scoreboardRoleChecker.canViewDefaultScoreboard(userJid, contest)) {
            visibleTabs.add(SCOREBOARD);
        }

        ContestState contestState;
        Optional<Duration> remainingContestStateDuration = Optional.empty();

        // TODO(fushar): refactor into separate "fetcher"

        if (contestTimer.hasFinished(contest, userJid)) {
            contestState = ContestState.FINISHED;
        } else if (contestTimer.hasStarted(contest, userJid)) {
            contestState = ContestState.STARTED;
            remainingContestStateDuration = Optional.of(contestTimer.getDurationToFinishTime(contest, userJid));
        } else if (contestTimer.hasBegun(contest)) {
            contestState = ContestState.BEGUN;
            remainingContestStateDuration = Optional.of(contestTimer.getDurationToEndTime(contest));
        } else {
            contestState = ContestState.NOT_BEGUN;
            remainingContestStateDuration = Optional.of(contestTimer.getDurationToBeginTime(contest));
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
