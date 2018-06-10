package judgels.uriel.contest.web;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.api.contest.web.ContestState.BEGUN;
import static judgels.uriel.api.contest.web.ContestState.FINISHED;
import static judgels.uriel.api.contest.web.ContestState.NOT_BEGUN;
import static judgels.uriel.api.contest.web.ContestState.STARTED;
import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.CLARIFICATIONS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.web.ContestState;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.role.RoleChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestWebConfigFetcherTests {
    private static final String USER = "userJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";

    private static final Duration TO_BEGIN = Duration.ofSeconds(1);
    private static final Duration TO_END = Duration.ofSeconds(2);
    private static final Duration TO_FINISH = Duration.ofSeconds(3);

    @Mock private RoleChecker roleChecker;
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
                announcementDao,
                clarificationDao,
                contestTimer);

        contest = new Contest.Builder()
                .id(1)
                .jid("jid")
                .name("name")
                .description("description")
                .style(ContestStyle.IOI)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.of(5, HOURS))
                .build();

        when(contestTimer.getDurationToBeginTime(contest)).thenReturn(TO_BEGIN);
        when(contestTimer.getDurationToEndTime(contest)).thenReturn(TO_END);
        when(contestTimer.getDurationToFinishTime(contest, USER)).thenReturn(TO_FINISH);

        when(roleChecker.canViewPublishedAnnouncements(USER, contest)).thenReturn(true);
        when(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewProblems(USER, contest)).thenReturn(true);
        when(roleChecker.canViewProblems(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewProblems(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewDefaultScoreboard(USER, contest)).thenReturn(true);
        when(roleChecker.canViewDefaultScoreboard(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewOwnSubmissions(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewOwnSubmissions(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewOwnClarifications(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewOwnClarifications(SUPERVISOR, contest)).thenReturn(true);
    }

    @Test
    void visible_tabs() {
        assertThat(webConfigFetcher.fetchConfig(USER, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD);

        assertThat(webConfigFetcher.fetchConfig(CONTESTANT, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, SUBMISSIONS, CLARIFICATIONS, SCOREBOARD);

        assertThat(webConfigFetcher.fetchConfig(SUPERVISOR, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, SUBMISSIONS, CLARIFICATIONS, SCOREBOARD);
    }

    @Test
    void states_and_durations() {
        assertStatesAndDurations(false, false, false, NOT_BEGUN, TO_BEGIN);
        assertStatesAndDurations(true, false, false, BEGUN, TO_END);
        assertStatesAndDurations(true, true, false, STARTED, TO_FINISH);
        assertStatesAndDurations(true, true, true, FINISHED, null);
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
        assertThat(config.getContestState()).isEqualTo(state);
        assertThat(config.getRemainingContestStateDuration()).isEqualTo(Optional.ofNullable(duration));
    }
}
