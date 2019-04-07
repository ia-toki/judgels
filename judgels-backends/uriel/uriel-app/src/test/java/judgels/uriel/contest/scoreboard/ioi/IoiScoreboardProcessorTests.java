package judgels.uriel.contest.scoreboard.ioi;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class IoiScoreboardProcessorTests {
    @Mock private ObjectMapper mapper;
    private IoiScoreboardProcessor scoreboardProcessor = new IoiScoreboardProcessor();

    @BeforeEach
    void before() {
        initMocks(this);
    }

    @Test
    void test_pagination() {
        ScoreboardState state = new ScoreboardState.Builder()
                .addContestantJids("c1", "c2")
                .addProblemJids("p1", "p2")
                .addProblemAliases("A", "B")
                .build();

        List<IoiScoreboardEntry> fakeEntries = new ArrayList<>(134);
        for (int i = 0; i < 134; i++) {
            fakeEntries.add(mock(IoiScoreboardEntry.class));
        }

        IoiScoreboard ioiScoreboard = new IoiScoreboard.Builder()
                .state(state)
                .content(new IoiScoreboardContent.Builder()
                        .entries(fakeEntries)
                        .build())
                .build();

        IoiScoreboard pagedScoreboard = (IoiScoreboard) scoreboardProcessor.paginate(ioiScoreboard, 1, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(0, 50));

        pagedScoreboard = (IoiScoreboard) scoreboardProcessor.paginate(ioiScoreboard, 2, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(50, 100));

        pagedScoreboard = (IoiScoreboard) scoreboardProcessor.paginate(ioiScoreboard, 3, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(100, 134));
    }

    @Nested
    class ComputeToString {
        private ScoreboardState state = new ScoreboardState.Builder()
                .addContestantJids("c1", "c2")
                .addProblemJids("p1", "p2")
                .addProblemAliases("A", "B")
                .build();

        private Contest contest = new Contest.Builder()
                .beginTime(Instant.ofEpochMilli(5))
                .duration(Duration.ofMillis(100))
                .id(1)
                .jid("JIDC")
                .name("contest-name")
                .slug("contest-slug")
                .style(ContestStyle.IOI)
                .build();

        private StyleModuleConfig styleModulesConfig = new IoiStyleModuleConfig.Builder().build();

        private Map<String, Optional<Instant>> contestantStartTimesMap = ImmutableMap.of(
                "c1", Optional.empty(),
                "c2", Optional.of(Instant.ofEpochMilli(10)),
                "c3", Optional.empty()
        );

        @BeforeEach
        void before() throws JsonProcessingException {
            when(mapper.writeValueAsString(any())).thenReturn("scoreboard-string");
        }

        @Test
        void only_count_contestant() throws JsonProcessingException {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c3")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(78)
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build()
            );

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModulesConfig,
                    contestantStartTimesMap,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                    .state(state)
                    .content(new IoiScoreboardContent.Builder()
                            .addEntries(new IoiScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .addScores(
                                            Optional.empty(),
                                            Optional.empty()
                                    )
                                    .totalScores(0)
                                    .lastAffectingPenalty(0)
                                    .build())
                            .addEntries(new IoiScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .addScores(
                                            Optional.empty(),
                                            Optional.empty()
                                    )
                                    .totalScores(0)
                                    .lastAffectingPenalty(0)
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void ignore_other_problem() throws JsonProcessingException {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c1")
                            .problemJid("p4")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(78)
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build()
            );

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModulesConfig,
                    contestantStartTimesMap,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                    .state(state)
                    .content(new IoiScoreboardContent.Builder()
                            .addEntries(new IoiScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .addScores(
                                            Optional.empty(),
                                            Optional.empty()
                                    )
                                    .totalScores(0)
                                    .lastAffectingPenalty(0)
                                    .build())
                            .addEntries(new IoiScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .addScores(
                                            Optional.empty(),
                                            Optional.empty()
                                    )
                                    .totalScores(0)
                                    .lastAffectingPenalty(0)
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void time_calculation() throws JsonProcessingException {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(78)
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c1")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(50)
                                    .verdict(Verdict.OK)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(3)
                            .jid("JIDS-3")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(25))
                            .userJid("c1")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(0)
                                    .verdict(Verdict.WRONG_ANSWER)
                                    .build())
                            .build()
            );

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModulesConfig,
                    contestantStartTimesMap,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                    .state(state)
                    .content(new IoiScoreboardContent.Builder()
                            .addEntries(new IoiScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .addScores(
                                            Optional.of(78),
                                            Optional.empty()
                                    )
                                    .totalScores(78)
                                    .lastAffectingPenalty(10)
                                    .build())
                            .addEntries(new IoiScoreboardEntry.Builder()
                                    .rank(2)
                                    .contestantJid("c1")
                                    .addScores(
                                            Optional.of(0),
                                            Optional.of(50)
                                    )
                                    .totalScores(50)
                                    .lastAffectingPenalty(15)
                                    .build())
                            .build())
                    .build());
        }

        @Nested
        class ProblemOrdering {
            private List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(50)
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c1")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(50)
                                    .verdict(Verdict.OK)
                                    .build())
                            .build()
            );

            @Test
            void base_case() throws JsonProcessingException {
                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModulesConfig,
                        contestantStartTimesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                        .state(state)
                        .content(new IoiScoreboardContent.Builder()
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .addScores(
                                                Optional.of(50),
                                                Optional.empty()
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(10)
                                        .build())
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .addScores(
                                                Optional.empty(),
                                                Optional.of(50)
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(15)
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void reversed_case() throws JsonProcessingException {
                state = new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2")
                        .addProblemJids("p2", "p1")
                        .addProblemAliases("B", "A")
                        .build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModulesConfig,
                        contestantStartTimesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                        .state(state)
                        .content(new IoiScoreboardContent.Builder()
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .addScores(
                                                Optional.empty(),
                                                Optional.of(50)
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(10)
                                        .build())
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .addScores(
                                                Optional.of(50),
                                                Optional.empty()
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(15)
                                        .build())
                                .build())
                        .build());
            }
        }

        @Nested
        class LastAffectingPenalty {
            private List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(50)
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochMilli(20))
                            .userJid("c1")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(50)
                                    .verdict(Verdict.OK)
                                    .build())
                            .build()
            );

            @Test
            void sorted_without_last_affecting_penalty() throws JsonProcessingException {
                styleModulesConfig = new IoiStyleModuleConfig.Builder().usingLastAffectingPenalty(false).build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModulesConfig,
                        contestantStartTimesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                        .state(state)
                        .content(new IoiScoreboardContent.Builder()
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .addScores(
                                                Optional.of(50),
                                                Optional.empty()
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(10)
                                        .build())
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .addScores(
                                                Optional.empty(),
                                                Optional.of(50)
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(15)
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void sorted_with_last_affecting_penalty() throws JsonProcessingException {
                styleModulesConfig = new IoiStyleModuleConfig.Builder().usingLastAffectingPenalty(true).build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModulesConfig,
                        contestantStartTimesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                verify(mapper).writeValueAsString(new IoiScoreboard.Builder()
                        .state(state)
                        .content(new IoiScoreboardContent.Builder()
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .addScores(
                                                Optional.of(50),
                                                Optional.empty()
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(10)
                                        .build())
                                .addEntries(new IoiScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .addScores(
                                                Optional.empty(),
                                                Optional.of(50)
                                        )
                                        .totalScores(50)
                                        .lastAffectingPenalty(15)
                                        .build())
                                .build())
                        .build());
            }
        }
    }

    @Test
    void filter_contestant_jids() {
        IoiScoreboardEntry entry = new IoiScoreboardEntry.Builder()
                .rank(0)
                .contestantJid("123")
                .totalScores(100)
                .lastAffectingPenalty(12)
                .build();

        IoiScoreboard scoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3", "c4")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new IoiScoreboardContent.Builder()
                        .addEntries(
                                new IoiScoreboardEntry.Builder().from(entry).rank(1).contestantJid("c1").build(),
                                new IoiScoreboardEntry.Builder().from(entry).rank(2).contestantJid("c2").build(),
                                new IoiScoreboardEntry.Builder().from(entry).rank(3).contestantJid("c3").build(),
                                new IoiScoreboardEntry.Builder().from(entry).rank(4).contestantJid("c4").build())
                        .build())
                .build();

        IoiScoreboard filteredScoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c3")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new IoiScoreboardContent.Builder()
                        .addEntries(
                                new IoiScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c1").build(),
                                new IoiScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c3").build())
                        .build())
                .build();

        assertThat(scoreboardProcessor.filterContestantJids(scoreboard, ImmutableSet.of("c1", "c3")))
                .isEqualTo(filteredScoreboard);
    }

    @Test
    void filter_problem_jids() {
        IoiScoreboardEntry entry = new IoiScoreboardEntry.Builder()
                .rank(0)
                .contestantJid("123")
                .totalScores(100)
                .lastAffectingPenalty(12)
                .build();

        IoiScoreboard scoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3")
                        .addProblemJids("p1", "p2", "p3", "p4")
                        .addProblemAliases("A", "B", "C", "D")
                        .build())
                .content(new IoiScoreboardContent.Builder()
                        .addEntries(
                                new IoiScoreboardEntry.Builder()
                                        .from(entry)
                                        .rank(1)
                                        .contestantJid("c1")
                                        .addScores(of(10), of(20), of(30), of(40))
                                        .totalScores(100)
                                        .build(),
                                new IoiScoreboardEntry.Builder()
                                        .from(entry)
                                        .rank(2)
                                        .contestantJid("c2")
                                        .addScores(of(10), of(10), of(30), of(20))
                                        .totalScores(70)
                                        .build(),
                                new IoiScoreboardEntry.Builder()
                                        .from(entry)
                                        .rank(2)
                                        .contestantJid("c3")
                                        .addScores(of(10), of(20), of(20), of(20))
                                        .totalScores(70)
                                        .build())
                        .build())
                .build();

        IoiScoreboard filteredScoreboard = new IoiScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3")
                        .addProblemJids("p1", "p3")
                        .addProblemAliases("A", "C")
                        .build())
                .content(new IoiScoreboardContent.Builder()
                        .addEntries(
                                new IoiScoreboardEntry.Builder()
                                        .from(entry)
                                        .rank(1)
                                        .contestantJid("c1")
                                        .addScores(of(10), of(30))
                                        .totalScores(40)
                                        .build(),
                                new IoiScoreboardEntry.Builder()
                                        .from(entry)
                                        .rank(1)
                                        .contestantJid("c2")
                                        .addScores(of(10), of(30))
                                        .totalScores(40)
                                        .build(),
                                new IoiScoreboardEntry.Builder()
                                        .from(entry)
                                        .rank(3)
                                        .contestantJid("c3")
                                        .addScores(of(10), of(20))
                                        .totalScores(30)
                                        .build())
                        .build())
                .build();

        IoiStyleModuleConfig config = new IoiStyleModuleConfig.Builder()
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .build();
        assertThat(scoreboardProcessor.filterProblemJids(scoreboard, ImmutableSet.of("p1", "p3"), config))
                .isEqualTo(filteredScoreboard);
    }
}
