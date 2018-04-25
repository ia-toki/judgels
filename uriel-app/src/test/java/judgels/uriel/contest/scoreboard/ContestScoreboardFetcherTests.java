package judgels.uriel.contest.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
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

        Map<String, UserInfo> usersMap = ImmutableMap.of(
                "userJid1", new UserInfo.Builder().username("username1").build(),
                "userJid2", new UserInfo.Builder().username("username2").build());
        when(userService.findUsersByJids(ImmutableSet.of("userJid1", "userJid2")))
                .thenReturn(usersMap);

        assertThat(scoreboardFetcher.fetchScoreboard("contestJid", ContestStyle.ICPC, ContestScoreboardType.OFFICIAL))
                .isEqualTo(
                        new ContestScoreboardResponse.Builder()
                                .data(new ContestScoreboard.Builder()
                                        .type(ContestScoreboardType.OFFICIAL)
                                        .scoreboard(scoreboard)
                                        .build())
                                .usersMap(usersMap)
                                .build());
    }
}
