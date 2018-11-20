package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardFetcherTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";

    @Mock private ContestScoreboardTypeFetcher typeFetcher;
    @Mock private ContestScoreboardStore scoreboardStore;
    @Mock private ContestScoreboardBuilder scoreboardBuilder;

    @Mock private Scoreboard icpcScoreboard;

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

        contest = mock(Contest.class);
        when(contest.getJid()).thenReturn(CONTEST_JID);
        when(contest.getStyle()).thenReturn(ContestStyle.ICPC);

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

        icpcScoreboard = mock(IcpcScoreboard.class);

        officialScoreboard = new ContestScoreboard.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .scoreboard(icpcScoreboard)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();
        frozenScoreboard = new ContestScoreboard.Builder()
                .type(ContestScoreboardType.FROZEN)
                .scoreboard(icpcScoreboard)
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

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, false, false, false))
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

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, false, false, false))
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

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, false, false, false))
                .contains(officialScoreboard);
    }
}
