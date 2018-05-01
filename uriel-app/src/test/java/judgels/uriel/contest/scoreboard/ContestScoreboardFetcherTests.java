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
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardFetcherTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";
    private static final boolean CAN_SUPERVISE_SCOREBOARD = true;

    @Mock private ContestScoreboardTypeFetcher typeFetcher;
    @Mock private ContestScoreboardStore scoreboardStore;
    @Mock private ContestScoreboardResponseBuilder responseBuilder;

    private ContestScoreboardFetcher scoreboardFetcher;
    private Contest contest;
    private RawContestScoreboard raw;
    private ContestScoreboardResponse response;

    @BeforeEach
    void before() {
        initMocks(this);

        scoreboardFetcher = new ContestScoreboardFetcher(typeFetcher, scoreboardStore, responseBuilder);

        contest = mock(Contest.class);
        when(contest.getJid()).thenReturn(CONTEST_JID);
        when(contest.getStyle()).thenReturn(ContestStyle.ICPC);

        raw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

        IcpcScoreboard scoreboard = mock(IcpcScoreboard.class);
        when(scoreboard.getState())
                .thenReturn(new ScoreboardState.Builder()
                        .addContestantJids("userJid1", "userJid2")
                        .build());

        response = new ContestScoreboardResponse.Builder()
                .data(new ContestScoreboard.Builder()
                        .type(ContestScoreboardType.OFFICIAL)
                        .scoreboard(scoreboard)
                        .updatedTime(Instant.ofEpochMilli(42))
                        .build())
                .build();

    }

    @Test
    void fetch_default_scoreboard() {
        when(typeFetcher.fetchViewableTypes(CONTEST_JID, CAN_SUPERVISE_SCOREBOARD))
                .thenReturn(ImmutableList.of(OFFICIAL, FROZEN));

        when(scoreboardStore.findScoreboard(CONTEST_JID, OFFICIAL))
                .thenReturn(Optional.of(raw));

        when(responseBuilder.buildResponse(contest, USER_JID, raw, OFFICIAL))
                .thenReturn(response);

        assertThat(scoreboardFetcher.fetchScoreboard(contest, USER_JID, CAN_SUPERVISE_SCOREBOARD))
                .contains(response);
    }

    @Test
    void fetch_frozen_scoreboard() {
        when(responseBuilder.buildResponse(contest, USER_JID, raw, FROZEN))
                .thenReturn(response);

        when(scoreboardStore.findScoreboard(CONTEST_JID, FROZEN))
                .thenReturn(Optional.of(raw));

        assertThat(scoreboardFetcher.fetchFrozenScoreboard(contest, USER_JID))
                .contains(response);
    }
}
