package judgels.uriel.contest.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ContestScoreboardFetcherTests {
    @Mock private ContestScoreboardStore scoreboardStore;
    @Mock private UserService userService;
    @Mock private ObjectMapper mapper;

    private ContestScoreboardFetcher scoreboardFetcher;

    @BeforeEach
    public void before() {
        initMocks(this);

        scoreboardFetcher = new ContestScoreboardFetcher(scoreboardStore, userService, mapper);
    }

    @Test
    public void fetch_scoreboard() throws IOException {
        when(scoreboardStore.findScoreboard("contestJid", ContestScoreboardType.OFFICIAL))
                .thenReturn(Optional.of(new ContestScoreboardData.Builder()
                        .scoreboard("json")
                        .type(ContestScoreboardType.OFFICIAL)
                        .build()));

        IcpcScoreboard scoreboard = mock(IcpcScoreboard.class);
        when(scoreboard.getState())
                .thenReturn(new ScoreboardState.Builder()
                        .addContestantJids("userJid1", "userJid2")
                        .build());

        when(mapper.readValue("json", IcpcScoreboard.class))
                .thenReturn(scoreboard);

        when(userService.findUsersByJids(ImmutableSet.of("userJid1", "userJid2")))
                .thenReturn(ImmutableMap.of(
                        "userJid1", new User.Builder().jid("userJid1").username("username1").build(),
                        "userJid2", new User.Builder().jid("userJid2").username("username2").build()));

        assertThat(scoreboardFetcher.fetchScoreboard("contestJid", ContestStyle.ICPC, ContestScoreboardType.OFFICIAL))
                .isEqualTo(new ContestScoreboard.Builder()
                        .type(ContestScoreboardType.OFFICIAL)
                        .scoreboard(scoreboard)
                        .contestantDisplayNames(ImmutableMap.of("userJid1", "username1", "userJid2", "username2"))
                        .build());
    }
}
