package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardFetcherTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";
    private static final boolean CAN_SUPERVISE_SCOREBOARD = true;

    @Mock private ContestScoreboardTypeFetcher typeFetcher;
    @Mock private ContestScoreboardStore scoreboardStore;
    @Mock private ContestScoreboardBuilder scoreboardBuilder;

    private ContestScoreboardFetcher scoreboardFetcher;
    private Contest contest;
    private RawContestScoreboard raw;
    private ContestScoreboard scoreboard;

    @BeforeEach
    void before() {
        initMocks(this);

        scoreboardFetcher = new ContestScoreboardFetcher(typeFetcher, scoreboardStore, scoreboardBuilder);

        contest = mock(Contest.class);
        when(contest.getJid()).thenReturn(CONTEST_JID);
        when(contest.getStyle()).thenReturn(ContestStyle.ICPC);

        raw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

        scoreboard = new ContestScoreboard.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .scoreboard(mock(IcpcScoreboard.class))
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

    }

    @Test
    void fetch_default_scoreboard() {
        when(typeFetcher.fetchViewableTypes(contest, CAN_SUPERVISE_SCOREBOARD))
                .thenReturn(ImmutableList.of(OFFICIAL, FROZEN));

        when(scoreboardStore.getScoreboard(CONTEST_JID, OFFICIAL))
                .thenReturn(Optional.of(raw));

        when(scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, OFFICIAL, CAN_SUPERVISE_SCOREBOARD))
                .thenReturn(scoreboard);

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, CAN_SUPERVISE_SCOREBOARD))
                .contains(scoreboard);
    }

    @Test
    void fetch_frozen_scoreboard() {
        when(scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, FROZEN, CAN_SUPERVISE_SCOREBOARD))
                .thenReturn(scoreboard);

        when(scoreboardStore.getScoreboard(CONTEST_JID, FROZEN))
                .thenReturn(Optional.of(raw));

        assertThat(scoreboardFetcher.fetchFrozenScoreboard(contest, USER_JID, CAN_SUPERVISE_SCOREBOARD))
                .contains(scoreboard);
    }
}
