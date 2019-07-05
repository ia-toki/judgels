package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardFetcherTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";

    @Mock private ContestScoreboardTypeFetcher typeFetcher;
    @Mock private ContestScoreboardStore scoreboardStore;
    @Mock private ContestScoreboardBuilder scoreboardBuilder;

    private Scoreboard icpcScoreboard;
    private ContestScoreboardFetcher scoreboardFetcher;
    private Contest contest;
    private RawContestScoreboard officialRaw;
    private RawContestScoreboard frozenRaw;
    private ContestScoreboard officialScoreboard;
    private ContestScoreboard frozenScoreboard;

    @BeforeEach
    void before() {
        initMocks(this);

        scoreboardFetcher = new ContestScoreboardFetcher(typeFetcher, scoreboardStore, scoreboardBuilder);

        contest = new Contest.Builder()
                .id(1)
                .jid(CONTEST_JID)
                .slug("slug")
                .style(ContestStyle.ICPC)
                .name("name")
                .beginTime(Instant.MIN)
                .duration(Duration.ZERO)
                .build();

        officialRaw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();
        frozenRaw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(FROZEN)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

        icpcScoreboard = new IcpcScoreboard.Builder()
                .state(new ScoreboardState.Builder().build())
                .content(new IcpcScoreboardContent.Builder()
                        .addEntries(mock(IcpcScoreboardEntry.class), mock(IcpcScoreboardEntry.class)).build())
                .build();

        officialScoreboard = new ContestScoreboard.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .style(ContestStyle.ICPC)
                .scoreboard(icpcScoreboard)
                .totalEntries(2)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();
        frozenScoreboard = new ContestScoreboard.Builder()
                .type(ContestScoreboardType.FROZEN)
                .style(ContestStyle.ICPC)
                .scoreboard(icpcScoreboard)
                .totalEntries(2)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();
    }

    @Test
    void fetch_official_scoreboard() {
        when(typeFetcher.fetchDefaultType(contest, false))
                .thenReturn(OFFICIAL);

        when(scoreboardStore.getScoreboard(CONTEST_JID, OFFICIAL))
                .thenReturn(Optional.of(officialRaw));

        when(scoreboardBuilder.buildScoreboard(officialRaw, contest, USER_JID, false, false))
                .thenReturn(icpcScoreboard);

        when(scoreboardBuilder.paginateScoreboard(icpcScoreboard, contest, 1, 50))
                .thenReturn(icpcScoreboard);

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, false, false, false, 1, 50))
                .contains(officialScoreboard);
    }

    @Test
    void fetch_frozen_scoreboard() {
        when(typeFetcher.fetchDefaultType(contest, false))
                .thenReturn(FROZEN);

        when(scoreboardStore.getScoreboard(CONTEST_JID, FROZEN))
                .thenReturn(Optional.of(frozenRaw));

        when(scoreboardBuilder.buildScoreboard(frozenRaw, contest, USER_JID, false, false))
                .thenReturn(icpcScoreboard);

        when(scoreboardBuilder.paginateScoreboard(icpcScoreboard, contest, 1, 50))
                .thenReturn(icpcScoreboard);

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, false, false, false, 1, 50))
                .contains(frozenScoreboard);
    }

    @Test
    void fetch_frozen_scoreboard_not_available() {
        when(typeFetcher.fetchDefaultType(contest, false))
                .thenReturn(FROZEN);

        when(scoreboardStore.getScoreboard(CONTEST_JID, OFFICIAL))
                .thenReturn(Optional.of(officialRaw));

        when(scoreboardBuilder.buildScoreboard(officialRaw, contest, USER_JID, false, false))
                .thenReturn(icpcScoreboard);

        when(scoreboardBuilder.paginateScoreboard(icpcScoreboard, contest, 1, 50))
                .thenReturn(icpcScoreboard);

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, false, false, false, 1, 50))
                .contains(officialScoreboard);
    }
}
