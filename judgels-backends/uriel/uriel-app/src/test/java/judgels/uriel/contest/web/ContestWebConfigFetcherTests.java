package judgels.uriel.contest.web;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.api.contest.web.ContestState.BEGUN;
import static judgels.uriel.api.contest.web.ContestState.FINISHED;
import static judgels.uriel.api.contest.web.ContestState.NOT_BEGUN;
import static judgels.uriel.api.contest.web.ContestState.PAUSED;
import static judgels.uriel.api.contest.web.ContestState.STARTED;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.api.contest.web.ContestState;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestWebConfigFetcherTests {
    private static final String USER = "userJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";
    private static final String MANAGER = "managerJid";
    private static final String ADMIN = "adminJid";

    private static final Duration TO_BEGIN = Duration.ofSeconds(1);
    private static final Duration TO_END = Duration.ofSeconds(2);
    private static final Duration TO_FINISH = Duration.ofSeconds(3);

    @Mock private ContestRoleChecker roleChecker;
    @Mock private ContestAnnouncementRoleChecker announcementRoleChecker;
    @Mock private ContestProblemRoleChecker problemRoleChecker;
    @Mock private ContestEditorialRoleChecker editorialRoleChecker;
    @Mock private ContestSubmissionRoleChecker submissionRoleChecker;
    @Mock private ContestClarificationRoleChecker clarificationRoleChecker;
    @Mock private ContestScoreboardRoleChecker scoreboardRoleChecker;
    @Mock private ContestContestantRoleChecker contestantRoleChecker;
    @Mock private ContestManagerRoleChecker managerRoleChecker;
    @Mock private ContestFileRoleChecker fileRoleChecker;
    @Mock private ContestAnnouncementDao announcementDao;
    @Mock private ContestClarificationDao clarificationDao;
    @Mock private ContestTimer contestTimer;

    private ContestWebConfigFetcher webConfigFetcher;
    private Contest contest;

    @BeforeEach
    void before() {
        initMocks(this);

        webConfigFetcher = new ContestWebConfigFetcher(
                roleChecker,
                announcementRoleChecker,
                problemRoleChecker,
                editorialRoleChecker,
                submissionRoleChecker,
                clarificationRoleChecker,
                scoreboardRoleChecker,
                contestantRoleChecker,
                managerRoleChecker,
                fileRoleChecker,
                announcementDao,
                clarificationDao,
                contestTimer);

        contest = new Contest.Builder()
                .id(1)
                .jid("jid")
                .slug("slug")
                .name("name")
                .style(ContestStyle.IOI)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.of(5, HOURS))
                .build();

        when(contestTimer.getDurationToBeginTime(contest)).thenReturn(TO_BEGIN);
        when(contestTimer.getDurationToEndTime(contest)).thenReturn(TO_END);
        when(contestTimer.getDurationToFinishTime(contest, USER)).thenReturn(TO_FINISH);

        when(roleChecker.getRole(ADMIN, contest)).thenReturn(ContestRole.ADMIN);
        when(roleChecker.getRole(MANAGER, contest)).thenReturn(ContestRole.MANAGER);
        when(roleChecker.getRole(SUPERVISOR, contest)).thenReturn(ContestRole.SUPERVISOR);
        when(roleChecker.getRole(CONTESTANT, contest)).thenReturn(ContestRole.CONTESTANT);
        when(roleChecker.getRole(USER, contest)).thenReturn(ContestRole.CONTESTANT);

        when(roleChecker.canAdminister(ADMIN)).thenReturn(true);
        when(roleChecker.canManage(ADMIN, contest)).thenReturn(true);
        when(roleChecker.canManage(MANAGER, contest)).thenReturn(true);

        when(problemRoleChecker.canView(USER, contest)).thenReturn(true);
        when(problemRoleChecker.canView(CONTESTANT, contest)).thenReturn(true);
        when(problemRoleChecker.canView(SUPERVISOR, contest)).thenReturn(true);
        when(problemRoleChecker.canView(MANAGER, contest)).thenReturn(true);
        when(problemRoleChecker.canView(ADMIN, contest)).thenReturn(true);

        when(editorialRoleChecker.canView(contest)).thenReturn(true);

        when(scoreboardRoleChecker.canViewDefault(USER, contest)).thenReturn(true);
        when(scoreboardRoleChecker.canViewDefault(CONTESTANT, contest)).thenReturn(true);
        when(scoreboardRoleChecker.canViewDefault(SUPERVISOR, contest)).thenReturn(true);
        when(scoreboardRoleChecker.canViewDefault(MANAGER, contest)).thenReturn(true);
        when(scoreboardRoleChecker.canViewDefault(ADMIN, contest)).thenReturn(true);

        when(submissionRoleChecker.canViewOwn(CONTESTANT, contest)).thenReturn(true);
        when(submissionRoleChecker.canViewOwn(SUPERVISOR, contest)).thenReturn(true);
        when(submissionRoleChecker.canViewOwn(MANAGER, contest)).thenReturn(true);
        when(submissionRoleChecker.canViewOwn(ADMIN, contest)).thenReturn(true);

        when(clarificationRoleChecker.canViewOwn(CONTESTANT, contest)).thenReturn(true);
        when(clarificationRoleChecker.canViewOwn(SUPERVISOR, contest)).thenReturn(true);
        when(clarificationRoleChecker.canViewOwn(MANAGER, contest)).thenReturn(true);
        when(clarificationRoleChecker.canViewOwn(ADMIN, contest)).thenReturn(true);

        when(contestantRoleChecker.canSupervise(SUPERVISOR, contest)).thenReturn(true);
        when(contestantRoleChecker.canSupervise(MANAGER, contest)).thenReturn(true);
        when(contestantRoleChecker.canSupervise(ADMIN, contest)).thenReturn(true);

        when(contestantRoleChecker.canManage(MANAGER, contest)).thenReturn(true);
        when(contestantRoleChecker.canManage(ADMIN, contest)).thenReturn(true);

        when(managerRoleChecker.canView(MANAGER, contest)).thenReturn(true);
        when(managerRoleChecker.canView(ADMIN, contest)).thenReturn(true);

        when(fileRoleChecker.canSupervise(SUPERVISOR, contest)).thenReturn(true);
        when(fileRoleChecker.canSupervise(MANAGER, contest)).thenReturn(true);
        when(fileRoleChecker.canSupervise(ADMIN, contest)).thenReturn(true);
    }

    @Test
    void visible_tabs() {
        assertThat(webConfigFetcher.fetchConfig(USER, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, EDITORIAL, SCOREBOARD);

        assertThat(webConfigFetcher.fetchConfig(CONTESTANT, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, EDITORIAL, SUBMISSIONS, CLARIFICATIONS, SCOREBOARD);

        assertThat(webConfigFetcher.fetchConfig(SUPERVISOR, contest).getVisibleTabs()).containsExactly(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD,
                FILES);

        assertThat(webConfigFetcher.fetchConfig(MANAGER, contest).getVisibleTabs()).containsExactly(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD,
                FILES,
                LOGS);

        assertThat(webConfigFetcher.fetchConfig(ADMIN, contest).getVisibleTabs()).containsExactly(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD,
                FILES,
                LOGS);
    }

    @Test
    void states_and_durations() {
        assertStatesAndDurations(false, false, false, NOT_BEGUN, TO_BEGIN);
        assertStatesAndDurations(true, false, false, BEGUN, TO_END);
        assertStatesAndDurations(true, true, false, STARTED, TO_FINISH);
        assertStatesAndDurations(true, true, true, FINISHED, null);

        when(contestTimer.isPaused(contest)).thenReturn(true);

        assertStatesAndDurations(false, false, false, PAUSED, null);
        assertStatesAndDurations(true, false, false, PAUSED, null);
        assertStatesAndDurations(true, true, false, PAUSED, null);
        assertStatesAndDurations(true, true, true, PAUSED, null);
    }

    private void assertStatesAndDurations(
            boolean begun,
            boolean started,
            boolean finished,
            ContestState state,
            Duration duration) {

        when(contestTimer.hasBegun(contest)).thenReturn(begun);
        when(contestTimer.hasStarted(contest, USER)).thenReturn(started);
        when(contestTimer.hasFinished(contest, USER)).thenReturn(finished);

        ContestWebConfig config = webConfigFetcher.fetchConfig(USER, contest);
        assertThat(config.getState()).isEqualTo(state);
        assertThat(config.getRemainingStateDuration()).isEqualTo(Optional.ofNullable(duration));
    }
}
