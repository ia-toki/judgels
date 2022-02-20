package judgels.uriel.contest.web;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.api.contest.web.ContestState.BEGUN;
import static judgels.uriel.api.contest.web.ContestState.FINISHED;
import static judgels.uriel.api.contest.web.ContestState.NOT_BEGUN;
import static judgels.uriel.api.contest.web.ContestState.PAUSED;
import static judgels.uriel.api.contest.web.ContestState.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        when(roleChecker.getRole(any(), eq(contest))).thenReturn(ContestRole.NONE);
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
