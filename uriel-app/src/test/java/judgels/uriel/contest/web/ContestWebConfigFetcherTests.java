package judgels.uriel.contest.web;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Duration;
import java.time.Instant;
import judgels.persistence.FixedClock;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.role.RoleChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestWebConfigFetcherTests {
    private static final String USER = "userJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";

    @Mock private RoleChecker roleChecker;

    private ContestWebConfigFetcher webConfigFetcher;
    private Contest contest;

    @BeforeEach
    void before() {
        initMocks(this);

        webConfigFetcher = new ContestWebConfigFetcher(roleChecker, new FixedClock());
        contest = new Contest.Builder()
                .id(1)
                .jid("jid")
                .name("name")
                .description("description")
                .style(ContestStyle.IOI)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.of(5, HOURS))
                .build();

        when(roleChecker.canViewAnnouncements(USER, contest)).thenReturn(true);
        when(roleChecker.canViewAnnouncements(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewAnnouncements(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewProblems(USER, contest)).thenReturn(true);
        when(roleChecker.canViewProblems(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewProblems(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewDefaultScoreboard(USER, contest)).thenReturn(true);
        when(roleChecker.canViewDefaultScoreboard(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contest)).thenReturn(true);

        when(roleChecker.canViewOwnSubmissions(CONTESTANT, contest)).thenReturn(true);
        when(roleChecker.canViewOwnSubmissions(SUPERVISOR, contest)).thenReturn(true);
    }

    @Test
    void visible_tabs() {
        assertThat(webConfigFetcher.fetchConfig(USER, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD);

        assertThat(webConfigFetcher.fetchConfig(CONTESTANT, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD, SUBMISSIONS);

        assertThat(webConfigFetcher.fetchConfig(SUPERVISOR, contest).getVisibleTabs())
                .containsExactly(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD, SUBMISSIONS);
    }
}
