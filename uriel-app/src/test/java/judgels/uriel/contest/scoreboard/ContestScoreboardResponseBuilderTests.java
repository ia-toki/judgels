package judgels.uriel.contest.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.module.ContestModuleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardResponseBuilderTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";

    @Mock private ContestModuleStore moduleStore;
    @Mock private UserService userService;
    @Mock private ObjectMapper mapper;

    private ContestScoreboardResponseBuilder responseBuilder;
    private Contest contest;

    @BeforeEach
    void before() {
        initMocks(this);

        responseBuilder = new ContestScoreboardResponseBuilder(moduleStore, userService, mapper);

        contest = mock(Contest.class);
        when(contest.getJid()).thenReturn(CONTEST_JID);
        when(contest.getStyle()).thenReturn(ContestStyle.ICPC);
    }

    @Test
    void when_incognito() throws IOException {
        RawContestScoreboard raw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(ContestScoreboardType.OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

        IcpcScoreboard scoreboard = mock(IcpcScoreboard.class);
        when(scoreboard.getState())
                .thenReturn(new ScoreboardState.Builder()
                        .addContestantJids("userJid1", "userJid2")
                        .build());

        IcpcScoreboard incognitoScoreboard = mock(IcpcScoreboard.class);
        when(incognitoScoreboard.getState())
                .thenReturn(new ScoreboardState.Builder()
                        .addContestantJids("userJid1")
                        .build());
        when(scoreboard.filterContestantJids(anySet())).thenReturn(incognitoScoreboard);

        when(moduleStore.getScoreboardModuleConfig(CONTEST_JID)).thenReturn(Optional.of(
                new ScoreboardModuleConfig.Builder()
                        .isIncognitoScoreboard(true)
                        .build()));

        when(mapper.readValue("json", IcpcScoreboard.class))
                .thenReturn(scoreboard);

        Map<String, UserInfo> usersMap = ImmutableMap.of(
                "userJid1", new UserInfo.Builder().username("username1").build());
        when(userService.findUsersByJids(ImmutableSet.of("userJid1")))
                .thenReturn(usersMap);

        assertThat(responseBuilder.buildResponse(contest, USER_JID, raw, ContestScoreboardType.OFFICIAL)).isEqualTo(
                new ContestScoreboardResponse.Builder()
                        .data(new ContestScoreboard.Builder()
                                .type(ContestScoreboardType.OFFICIAL)
                                .scoreboard(incognitoScoreboard)
                                .updatedTime(Instant.ofEpochMilli(42))
                                .build())
                        .usersMap(usersMap)
                        .build());
    }

    @Test
    void when_not_incognito() throws IOException {
        RawContestScoreboard raw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(ContestScoreboardType.OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

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

        assertThat(responseBuilder.buildResponse(contest, USER_JID, raw, ContestScoreboardType.OFFICIAL)).isEqualTo(
                new ContestScoreboardResponse.Builder()
                        .data(new ContestScoreboard.Builder()
                                .type(ContestScoreboardType.OFFICIAL)
                                .scoreboard(scoreboard)
                                .updatedTime(Instant.ofEpochMilli(42))
                                .build())
                        .usersMap(usersMap)
                        .build());
    }
}
