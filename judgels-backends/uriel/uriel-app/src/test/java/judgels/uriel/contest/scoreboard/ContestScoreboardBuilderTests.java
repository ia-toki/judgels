package judgels.uriel.contest.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import judgels.gabriel.api.LanguageRestriction;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ioi.IoiScoreboardProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardBuilderTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";

    @Mock private ScoreboardProcessorRegistry processorRegistry;
    @Mock private ScoreboardProcessor scoreboardProcessor;
    @Mock private ContestModuleStore moduleStore;
    @Mock private ContestProblemStore problemStore;
    @Mock private ObjectMapper mapper;

    private ContestScoreboardBuilder scoreboardBuilder;

    private Contest contest;
    private RawContestScoreboard raw;
    private Scoreboard scoreboard;

    @BeforeEach
    void before() {
        initMocks(this);

        scoreboardBuilder = new ContestScoreboardBuilder(processorRegistry, moduleStore, problemStore, mapper);

        when(processorRegistry.get(any())).thenReturn(scoreboardProcessor);

        contest = mock(Contest.class);
        when(contest.getJid()).thenReturn(CONTEST_JID);
        when(contest.getStyle()).thenReturn(ContestStyle.ICPC);

        when(moduleStore.getScoreboardModuleConfig(CONTEST_JID)).thenReturn(
                new ScoreboardModuleConfig.Builder()
                        .isIncognitoScoreboard(false)
                        .build());

        raw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(ContestScoreboardType.OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();

        scoreboard = mock(Scoreboard.class);
        when(scoreboardProcessor.parseFromString(mapper, "json")).thenReturn(scoreboard);
    }


    @Test
    void test_pagination() {
        List<IcpcScoreboard.IcpcScoreboardEntry> fakeEntries = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            fakeEntries.add(mock(IcpcScoreboard.IcpcScoreboardEntry.class));
        }

        IcpcScoreboard icpcScoreboard = mock(IcpcScoreboard.class);
        when(icpcScoreboard.getContent()).thenReturn(
                new IcpcScoreboard.IcpcScoreboardContent.Builder()
                        .entries(fakeEntries)
                        .build());

        when(scoreboardProcessor.getTotalEntries(icpcScoreboard)).thenReturn(100);
        when(scoreboardProcessor.paginate(icpcScoreboard, 1, 50)).thenReturn(scoreboard);

        assertThat(scoreboardBuilder.paginateScoreboard(icpcScoreboard, contest, 1, 50)).isEqualTo(scoreboard);
    }

    @Test
    void when_incognito() {
        Scoreboard incognitoScoreboard = mock(Scoreboard.class);
        when(incognitoScoreboard.getState())
                .thenReturn(new ScoreboardState.Builder()
                        .addContestantJids("userJid1")
                        .build());

        when(moduleStore.getScoreboardModuleConfig(CONTEST_JID)).thenReturn(
                new ScoreboardModuleConfig.Builder()
                        .isIncognitoScoreboard(true)
                        .build());

        when(scoreboardProcessor.filterContestantJids(eq(scoreboard), anySet())).thenReturn(incognitoScoreboard);

        assertThat(scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, false, true))
                .isEqualTo(incognitoScoreboard);
        assertThat(scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, true, true))
                .isEqualTo(scoreboard);
    }

    @Test
    void when_not_incognito() {
        assertThat(scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, false, true))
                .isEqualTo(scoreboard);
    }

    @Test
    void when_problems_are_filtered() {
        when(contest.getStyle()).thenReturn(ContestStyle.IOI);

        IoiScoreboardProcessor ioiScoreboardProcessor = mock(IoiScoreboardProcessor.class);
        when(processorRegistry.get(ContestStyle.IOI)).thenReturn(ioiScoreboardProcessor);

        IoiStyleModuleConfig config = new IoiStyleModuleConfig.Builder()
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .build();
        when(moduleStore.getIoiStyleModuleConfig(CONTEST_JID)).thenReturn(config);

        IoiScoreboard ioiScoreboard = mock(IoiScoreboard.class);
        when(ioiScoreboard.getState()).thenReturn(new ScoreboardState.Builder()
                .addProblemJids("p1", "p2", "p3")
                .addProblemAliases("A", "B", "C")
                .build());

        when(ioiScoreboardProcessor.parseFromString(mapper, "json")).thenReturn(ioiScoreboard);

        when(problemStore.getOpenProblemJids(CONTEST_JID)).thenReturn(ImmutableList.of("p1", "p3"));

        IoiScoreboard filteredScoreboard = mock(IoiScoreboard.class);
        when(ioiScoreboardProcessor.filterProblemJids(eq(ioiScoreboard), anySet(), any()))
                .thenReturn(filteredScoreboard);

        assertThat(scoreboardBuilder
                .buildScoreboard(raw, contest, USER_JID, false, false))
                .isEqualTo(filteredScoreboard);
    }
}
