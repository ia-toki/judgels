package judgels.uriel.contest.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.LanguageRestriction;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardBuilderTests {
    private static final String CONTEST_JID = "contestJid";
    private static final String USER_JID = "userJid";

    private ScoreboardProcessorRegistry processorRegistry;
    @Mock private ContestModuleStore moduleStore;
    @Mock private ContestProblemStore problemStore;
    @Mock private ObjectMapper mapper;

    private ContestScoreboardBuilder scoreboardBuilder;

    private Contest contest;
    private RawContestScoreboard raw;

    @BeforeEach
    void before() {
        initMocks(this);

        processorRegistry = new ScoreboardProcessorRegistry();

        scoreboardBuilder = new ContestScoreboardBuilder(processorRegistry, moduleStore, problemStore, mapper);

        contest = new Contest.Builder()
                .id(1)
                .jid(CONTEST_JID)
                .slug("slug")
                .style(ContestStyle.IOI)
                .name("name")
                .beginTime(Instant.MIN)
                .duration(Duration.ZERO)
                .build();

        when(moduleStore.getScoreboardModuleConfig(CONTEST_JID)).thenReturn(
                new ScoreboardModuleConfig.Builder()
                        .isIncognitoScoreboard(false)
                        .build());

        raw = new RawContestScoreboard.Builder()
                .scoreboard("json")
                .type(ContestScoreboardType.OFFICIAL)
                .updatedTime(Instant.ofEpochMilli(42))
                .build();
    }


    @Test
    void paginate_scoreboard() {
        List<IoiScoreboardEntry> entries = new ArrayList<>(100);
        for (int i = 0; i < 5; i++) {
            IoiScoreboardEntry entry = mock(IoiScoreboardEntry.class);
            when(entry.getRank()).thenReturn(i);
            entries.add(entry);
        }

        IoiScoreboard scoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder().build())
                .content(new IoiScoreboardContent.Builder().entries(entries).build())
                .build();

        Scoreboard page1 = scoreboardBuilder.paginateScoreboard(scoreboard, contest, 1, 3);
        assertThat(page1.getContent().getEntries().size()).isEqualTo(3);
        assertThat(page1.getContent().getEntries().get(0).getRank()).isEqualTo(0);
        assertThat(page1.getContent().getEntries().get(1).getRank()).isEqualTo(1);
        assertThat(page1.getContent().getEntries().get(2).getRank()).isEqualTo(2);

        Scoreboard page2 = scoreboardBuilder.paginateScoreboard(scoreboard, contest, 2, 3);
        assertThat(page2.getContent().getEntries().size()).isEqualTo(2);
        assertThat(page2.getContent().getEntries().get(0).getRank()).isEqualTo(3);
        assertThat(page2.getContent().getEntries().get(1).getRank()).isEqualTo(4);
    }

    @Test
    void build_scoreboard_when_incognito() throws IOException {
        IoiScoreboardEntry entry1 = mock(IoiScoreboardEntry.class);
        when(entry1.getRank()).thenReturn(1);
        when(entry1.getContestantJid()).thenReturn("random-jid");
        when(entry1.getContestantUsername()).thenReturn("random-username");
        when(entry1.getContestantRating()).thenReturn(0);
        IoiScoreboardEntry entry2 = mock(IoiScoreboardEntry.class);
        when(entry2.getRank()).thenReturn(2);
        when(entry2.getContestantJid()).thenReturn(USER_JID);
        when(entry2.getContestantUsername()).thenReturn("username");
        when(entry2.getContestantRating()).thenReturn(0);
        List<IoiScoreboardEntry> entries = ImmutableList.of(entry1, entry2);

        IoiScoreboard scoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder().build())
                .content(new IoiScoreboardContent.Builder().entries(entries).build())
                .build();
        when(mapper.readValue(anyString(), eq(IoiScoreboard.class))).thenReturn(scoreboard);

        when(moduleStore.getScoreboardModuleConfig(CONTEST_JID)).thenReturn(
                new ScoreboardModuleConfig.Builder()
                        .isIncognitoScoreboard(true)
                        .build());

        Scoreboard incognitoScoreboard = scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, false, true);
        assertThat(incognitoScoreboard.getContent().getEntries().size()).isEqualTo(1);
        assertThat(incognitoScoreboard.getContent().getEntries().get(0).getRank()).isEqualTo(-1);
        assertThat(incognitoScoreboard.getContent().getEntries().get(0).getContestantJid()).isEqualTo(USER_JID);

        incognitoScoreboard = scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, true, true);
        assertThat(incognitoScoreboard.getContent().getEntries().size()).isEqualTo(2);
    }

    @Test
    void build_scoreboard_when_not_incognito() throws IOException {
        IoiScoreboardEntry entry1 = mock(IoiScoreboardEntry.class);
        when(entry1.getRank()).thenReturn(1);
        when(entry1.getContestantJid()).thenReturn("random-jid");
        when(entry1.getContestantUsername()).thenReturn("random-username");
        when(entry1.getContestantRating()).thenReturn(0);
        IoiScoreboardEntry entry2 = mock(IoiScoreboardEntry.class);
        when(entry2.getRank()).thenReturn(2);
        when(entry2.getContestantJid()).thenReturn(USER_JID);
        when(entry2.getContestantUsername()).thenReturn("username");
        when(entry2.getContestantRating()).thenReturn(0);
        List<IoiScoreboardEntry> entries = ImmutableList.of(entry1, entry2);

        IoiScoreboard scoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder().build())
                .content(new IoiScoreboardContent.Builder().entries(entries).build())
                .build();
        when(mapper.readValue(anyString(), eq(IoiScoreboard.class))).thenReturn(scoreboard);

        Scoreboard incognitoScoreboard = scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, false, true);
        assertThat(incognitoScoreboard.getContent().getEntries().size()).isEqualTo(2);

    }

    @Test
    void build_scoreboard_when_problems_are_filtered() throws IOException {
        IoiStyleModuleConfig config = new IoiStyleModuleConfig.Builder()
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .build();
        when(moduleStore.getIoiStyleModuleConfig(CONTEST_JID)).thenReturn(config);

        IoiScoreboard scoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addProblemJids("p1", "p2", "p3")
                        .addProblemAliases("A", "B", "C")
                        .build())
                .content(new IoiScoreboardContent.Builder().addEntries(
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("jid1")
                                .contestantUsername("jid1")
                                .contestantRating(0)
                                .addScores(Optional.of(6), Optional.of(8), Optional.empty())
                                .totalScores(14)
                                .lastAffectingPenalty(0)
                                .build(),
                        new IoiScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("jid2")
                                .contestantUsername("jid2")
                                .contestantRating(0)
                                .addScores(Optional.of(1), Optional.of(2), Optional.of(7))
                                .totalScores(10)
                                .lastAffectingPenalty(0)
                                .build()).build())
                .build();
        when(mapper.readValue(anyString(), eq(IoiScoreboard.class))).thenReturn(scoreboard);

        when(problemStore.getOpenProblemJids(CONTEST_JID)).thenReturn(ImmutableList.of("p1", "p3"));

        Scoreboard filteredScoreboard = scoreboardBuilder.buildScoreboard(raw, contest, USER_JID, false, false);
        assertThat(filteredScoreboard).isEqualTo(new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addProblemJids("p1", "p3")
                        .addProblemAliases("A", "C")
                        .build())
                .content(new IoiScoreboardContent.Builder().addEntries(
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("jid2")
                                .contestantUsername("jid2")
                                .contestantRating(0)
                                .addScores(Optional.of(1), Optional.of(7))
                                .totalScores(8)
                                .lastAffectingPenalty(0)
                                .build(),
                        new IoiScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("jid1")
                                .contestantUsername("jid1")
                                .contestantRating(0)
                                .addScores(Optional.of(6), Optional.empty())
                                .totalScores(6)
                                .lastAffectingPenalty(0)
                                .build()).build())
                .build());
    }
}
