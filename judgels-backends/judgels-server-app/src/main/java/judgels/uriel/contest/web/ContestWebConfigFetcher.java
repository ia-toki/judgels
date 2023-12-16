package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.CLARIFICATIONS;
import static judgels.uriel.api.contest.web.ContestTab.CONTESTANTS;
import static judgels.uriel.api.contest.web.ContestTab.EDITORIAL;
import static judgels.uriel.api.contest.web.ContestTab.FILES;
import static judgels.uriel.api.contest.web.ContestTab.LOGS;
import static judgels.uriel.api.contest.web.ContestTab.MANAGERS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;
import static judgels.uriel.api.contest.web.ContestTab.SUPERVISORS;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.api.contest.web.ContestState;
import judgels.uriel.api.contest.web.ContestTab;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.announcement.ContestAnnouncementRoleChecker;
import judgels.uriel.contest.clarification.ContestClarificationRoleChecker;
import judgels.uriel.contest.contestant.ContestContestantRoleChecker;
import judgels.uriel.contest.editorial.ContestEditorialRoleChecker;
import judgels.uriel.contest.file.ContestFileRoleChecker;
import judgels.uriel.contest.manager.ContestManagerRoleChecker;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.scoreboard.ContestScoreboardRoleChecker;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;

public class ContestWebConfigFetcher {
    private final ContestRoleChecker roleChecker;
    private final ContestAnnouncementRoleChecker announcementRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestEditorialRoleChecker editorialRoleChecker;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestClarificationRoleChecker clarificationRoleChecker;
    private final ContestScoreboardRoleChecker scoreboardRoleChecker;
    private final ContestContestantRoleChecker contestantRoleChecker;
    private final ContestManagerRoleChecker managerRoleChecker;
    private final ContestFileRoleChecker fileRoleChecker;
    private final ContestAnnouncementDao announcementDao;
    private final ContestClarificationDao clarificationDao;
    private final ContestTimer contestTimer;

    @Inject
    public ContestWebConfigFetcher(
            ContestRoleChecker roleChecker,
            ContestAnnouncementRoleChecker announcementRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestEditorialRoleChecker editorialRoleChecker,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestClarificationRoleChecker clarificationRoleChecker,
            ContestScoreboardRoleChecker scoreboardRoleChecker,
            ContestContestantRoleChecker contestantRoleChecker,
            ContestManagerRoleChecker managerRoleChecker,
            ContestFileRoleChecker fileRoleChecker,
            ContestAnnouncementDao announcementDao,
            ContestClarificationDao clarificationDao,
            ContestTimer contestTimer) {

        this.roleChecker = roleChecker;
        this.announcementRoleChecker = announcementRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.editorialRoleChecker = editorialRoleChecker;
        this.submissionRoleChecker = submissionRoleChecker;
        this.clarificationRoleChecker = clarificationRoleChecker;
        this.scoreboardRoleChecker = scoreboardRoleChecker;
        this.contestantRoleChecker = contestantRoleChecker;
        this.managerRoleChecker = managerRoleChecker;
        this.fileRoleChecker = fileRoleChecker;
        this.announcementDao = announcementDao;
        this.clarificationDao = clarificationDao;
        this.contestTimer = contestTimer;
    }

    public ContestWebConfig fetchConfig(String userJid, Contest contest) {
        ContestRole role = roleChecker.getRole(userJid, contest);

        boolean canManage = roleChecker.canManage(userJid, contest);

        ImmutableSet.Builder<ContestTab> visibleTabs = ImmutableSet.builder();
        visibleTabs.add(ANNOUNCEMENTS);

        if (problemRoleChecker.canView(userJid, contest)) {
            visibleTabs.add(PROBLEMS);
        }
        if (editorialRoleChecker.canView(contest)) {
            visibleTabs.add(EDITORIAL);
        }
        if (contestantRoleChecker.canSupervise(userJid, contest)) {
            visibleTabs.add(CONTESTANTS);
        }
        if (contestantRoleChecker.canManage(userJid, contest)) {
            visibleTabs.add(SUPERVISORS);
        }
        if (managerRoleChecker.canView(userJid, contest)) {
            visibleTabs.add(MANAGERS);
        }
        if (submissionRoleChecker.canViewOwn(userJid, contest)) {
            visibleTabs.add(SUBMISSIONS);
        }
        if (clarificationRoleChecker.canViewOwn(userJid, contest)) {
            visibleTabs.add(CLARIFICATIONS);
        }
        if (scoreboardRoleChecker.canViewDefault(userJid, contest)) {
            visibleTabs.add(SCOREBOARD);
        }
        if (fileRoleChecker.canSupervise(userJid, contest)) {
            visibleTabs.add(FILES);
        }
        if (canManage) {
            visibleTabs.add(LOGS);
        }

        ContestState state;
        Optional<Duration> remainingStateDuration = Optional.empty();

        // TODO(fushar): refactor into separate "fetcher"

        if (contestTimer.isPaused(contest)) {
            state = ContestState.PAUSED;
        } else if (contestTimer.hasFinished(contest, userJid)) {
            state = ContestState.FINISHED;
        } else if (contestTimer.hasStarted(contest, userJid)) {
            state = ContestState.STARTED;
            remainingStateDuration = Optional.of(contestTimer.getDurationToFinishTime(contest, userJid));
        } else if (contestTimer.hasBegun(contest)) {
            state = ContestState.BEGUN;
            remainingStateDuration = Optional.of(contestTimer.getDurationToEndTime(contest));
        } else {
            state = ContestState.NOT_BEGUN;
            remainingStateDuration = Optional.of(contestTimer.getDurationToBeginTime(contest));
        }

        String contestJid = contest.getJid();

        int announcementCount = 0;
        if (!announcementRoleChecker.canSupervise(userJid, contest)) {
            // TODO(fushar): remove this try block and fix the test so that it doesn't use mock for announcementDao
            try {
                announcementCount = announcementDao
                        .selectByContestJid(contestJid)
                        .whereStatusIs(ContestAnnouncementStatus.PUBLISHED.name())
                        .count();
            } catch (NullPointerException e) {
                // skip; for test only
            }
        }

        int clarificationCount = 0;
        ContestClarificationStatus clarificationStatus = ContestClarificationStatus.ASKED;
        try {
            // TODO(fushar): remove this try block and fix the test so that it doesn't use mock for clarificationDao
            if (clarificationRoleChecker.canManage(userJid, contest)) {
                clarificationStatus = ContestClarificationStatus.ASKED;
                clarificationCount = clarificationDao
                        .selectByContestJid(contestJid)
                        .whereStatusIs(clarificationStatus.name())
                        .count();
            } else {
                clarificationStatus = ContestClarificationStatus.ANSWERED;
                clarificationCount = clarificationDao
                        .selectByContestJid(contestJid)
                        .whereUserIsAsker(userJid)
                        .whereStatusIs(clarificationStatus.name())
                        .count();
            }
        } catch (NullPointerException e) {
            // skip; for test only
        }

        return new ContestWebConfig.Builder()
                .role(role)
                .canManage(canManage)
                .visibleTabs(visibleTabs.build())
                .state(state)
                .remainingStateDuration(remainingStateDuration)
                .announcementCount(announcementCount)
                .clarificationCount(clarificationCount)
                .clarificationStatus(clarificationStatus)
                .build();
    }
}
