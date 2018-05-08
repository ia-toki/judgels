package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.role.RoleChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestWebConfigFetcherTests {
    private static final String CONTEST = "contestJid";
    private static final String USER = "userJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";

    @Mock private RoleChecker roleChecker;

    private ContestWebConfigFetcher webConfigFetcher;

    @BeforeEach
    void before() {
        initMocks(this);

        webConfigFetcher = new ContestWebConfigFetcher(roleChecker);

        when(roleChecker.canViewOwnSubmissions(CONTESTANT, CONTEST)).thenReturn(true);
        when(roleChecker.canViewOwnSubmissions(SUPERVISOR, CONTEST)).thenReturn(true);
    }

    @Test
    void fetch_web_config() {
        assertThat(webConfigFetcher.getConfig(USER, CONTEST)).isEqualTo(new ContestWebConfig.Builder()
                .addVisibleTabs(ANNOUNCEMENTS, SCOREBOARD)
                .build());

        assertThat(webConfigFetcher.getConfig(CONTESTANT, CONTEST)).isEqualTo(new ContestWebConfig.Builder()
                .addVisibleTabs(ANNOUNCEMENTS, SCOREBOARD, SUBMISSIONS)
                .build());

        assertThat(webConfigFetcher.getConfig(SUPERVISOR, CONTEST)).isEqualTo(new ContestWebConfig.Builder()
                .addVisibleTabs(ANNOUNCEMENTS, SCOREBOARD, SUBMISSIONS)
                .build());
    }
}
