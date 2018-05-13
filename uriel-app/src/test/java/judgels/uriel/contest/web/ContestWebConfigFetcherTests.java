package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestWebConfig;
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

        webConfigFetcher = new ContestWebConfigFetcher(roleChecker);
        contest = mock(Contest.class);

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
    void fetch_web_config() {
        assertThat(webConfigFetcher.fetchConfig(USER, contest)).isEqualTo(new ContestWebConfig.Builder()
                .addVisibleTabs(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD)
                .build());

        assertThat(webConfigFetcher.fetchConfig(CONTESTANT, contest)).isEqualTo(new ContestWebConfig.Builder()
                .addVisibleTabs(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD, SUBMISSIONS)
                .build());

        assertThat(webConfigFetcher.fetchConfig(SUPERVISOR, contest)).isEqualTo(new ContestWebConfig.Builder()
                .addVisibleTabs(ANNOUNCEMENTS, PROBLEMS, SCOREBOARD, SUBMISSIONS)
                .build());
    }
}
