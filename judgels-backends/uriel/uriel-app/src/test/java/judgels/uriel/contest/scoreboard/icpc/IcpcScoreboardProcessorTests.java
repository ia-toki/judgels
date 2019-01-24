package judgels.uriel.contest.scoreboard.icpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.gabriel.api.Verdicts;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class IcpcScoreboardProcessorTests {
    @Mock private ObjectMapper mapper;
    private IcpcScoreboardProcessor scoreboardProcessor = new IcpcScoreboardProcessor();

    @BeforeEach
    void before() {
        initMocks(this);
    }

    @Nested
    class ComputeToString {
        private ScoreboardState state = new ScoreboardState.Builder()
                .addContestantJids("c1", "c2")
                .addProblemJids("p1", "p2")
                .addProblemAliases("A", "B")
                .build();

        private Contest contest = new Contest.Builder()
                .beginTime(Instant.ofEpochSecond(60))
                .duration(Duration.ofMinutes(100))
                .id(1)
                .jid("JIDC")
                .name("contest-name")
                .slug("contest-slug")
                .style(ContestStyle.ICPC)
                .build();

        private StyleModuleConfig styleModuleConfig = new IcpcStyleModuleConfig.Builder()
                        .wrongSubmissionPenalty(1000)
                        .build();

        private Map<String, Optional<Instant>> contestantStartTimesMap = ImmutableMap.of(
                "c1", Optional.empty(),
                "c2", Optional.of(Instant.ofEpochSecond(300)),
                "c3", Optional.empty()
        );

        @BeforeEach
        void before() throws JsonProcessingException {
            when(mapper.writeValueAsString(any())).thenReturn("scoreboard-string");
        }

        @Test
        void time_calculation() throws JsonProcessingException {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(300)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c1")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(360)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(360))
                            .userJid("c2")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(3)
                                    .jid("JIDG-4")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(400)
                            .jid("JIDS-3")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(400))
                            .userJid("c1")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(2)
                                    .jid("JIDG-3")
                                    .score(0)
                                    .verdict(Verdicts.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(410)
                            .jid("JIDS-4")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(410))
                            .userJid("c1")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(2)
                                    .jid("JIDG-3")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(900)
                            .jid("JIDS-5")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(900))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build()
            );

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    submissions);

            verify(mapper).writeValueAsString(new IcpcScoreboard.Builder()
                    .state(state)
                    .content(new IcpcScoreboardContent.Builder()
                            .addEntries(new IcpcScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .totalAccepted(2)
                                    .totalPenalties(11)
                                    .lastAcceptedPenalty(600000)
                                    .addAttemptsList(1, 1)
                                    .addPenaltyList(10, 1)
                                    .addProblemStateList(
                                            IcpcScoreboardProblemState.ACCEPTED,
                                            IcpcScoreboardProblemState.FIRST_ACCEPTED
                                    )
                                    .build())
                            .addEntries(new IcpcScoreboardEntry.Builder()
                                    .rank(2)
                                    .contestantJid("c1")
                                    .totalAccepted(2)
                                    .totalPenalties(1010)
                                    .lastAcceptedPenalty(350000)
                                    .addAttemptsList(1, 2)
                                    .addPenaltyList(4, 6)
                                    .addProblemStateList(
                                            IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                            IcpcScoreboardProblemState.ACCEPTED
                                    )
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
                            .time(Instant.ofEpochSecond(900))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c1")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build()
            );

            @Test
            void base_case() throws JsonProcessingException {
                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions);

                verify(mapper).writeValueAsString(new IcpcScoreboard.Builder()
                        .state(state)
                        .content(new IcpcScoreboardContent.Builder()
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .totalAccepted(1)
                                        .totalPenalties(4)
                                        .lastAcceptedPenalty(240000)
                                        .addAttemptsList(0, 1)
                                        .addPenaltyList(0, 4)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.NOT_ACCEPTED,
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c2")
                                        .totalAccepted(1)
                                        .totalPenalties(10)
                                        .lastAcceptedPenalty(600000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
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
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions);

                verify(mapper).writeValueAsString(new IcpcScoreboard.Builder()
                        .state(state)
                        .content(new IcpcScoreboardContent.Builder()
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .totalAccepted(1)
                                        .totalPenalties(4)
                                        .lastAcceptedPenalty(240000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(4, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c2")
                                        .totalAccepted(1)
                                        .totalPenalties(10)
                                        .lastAcceptedPenalty(600000)
                                        .addAttemptsList(0, 1)
                                        .addPenaltyList(0, 10)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.NOT_ACCEPTED,
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }
        }

        @Nested
        class Sorting {
            @Test
            void solve_over_penalty() throws JsonProcessingException {
                List<Submission> submissions = ImmutableList.of(
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(300)
                                .jid("JIDS-2")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(300))
                                .userJid("c1")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(360)
                                .jid("JIDS-4")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(360))
                                .userJid("c2")
                                .problemJid("p2")
                                .latestGrading(new Grading.Builder()
                                        .id(3)
                                        .jid("JIDG-4")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(900)
                                .jid("JIDS-1")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c2")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-1")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build()
                );

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions);

                verify(mapper).writeValueAsString(new IcpcScoreboard.Builder()
                        .state(state)
                        .content(new IcpcScoreboardContent.Builder()
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalAccepted(2)
                                        .totalPenalties(11)
                                        .lastAcceptedPenalty(600000)
                                        .addAttemptsList(1, 1)
                                        .addPenaltyList(10, 1)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.ACCEPTED,
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalAccepted(1)
                                        .totalPenalties(4)
                                        .lastAcceptedPenalty(240000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(4, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void penalty_as_tiebreaker() throws JsonProcessingException {
                List<Submission> submissions = ImmutableList.of(
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(1)
                                .jid("JIDS-1")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c2")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-1")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(1)
                                .jid("JIDS-2")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c1")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build()
                );

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions);

                verify(mapper).writeValueAsString(new IcpcScoreboard.Builder()
                        .state(state)
                        .content(new IcpcScoreboardContent.Builder()
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalAccepted(1)
                                        .totalPenalties(10)
                                        .lastAcceptedPenalty(600000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalAccepted(1)
                                        .totalPenalties(14)
                                        .lastAcceptedPenalty(840000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(14, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void same_rank_if_equal() throws JsonProcessingException {
                state = new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build();

                List<Submission> submissions = ImmutableList.of(
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(660)
                                .jid("JIDS-2")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(660))
                                .userJid("c1")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(900)
                                .jid("JIDS-1")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c2")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-1")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(900)
                                .jid("JIDS-3")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c3")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-1")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build()
                );

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions);

                verify(mapper).writeValueAsString(new IcpcScoreboard.Builder()
                        .state(state)
                        .content(new IcpcScoreboardContent.Builder()
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .totalAccepted(1)
                                        .totalPenalties(10)
                                        .lastAcceptedPenalty(600000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalAccepted(1)
                                        .totalPenalties(10)
                                        .lastAcceptedPenalty(600000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new IcpcScoreboardEntry.Builder()
                                        .rank(3)
                                        .contestantJid("c3")
                                        .totalAccepted(1)
                                        .totalPenalties(14)
                                        .lastAcceptedPenalty(840000)
                                        .addAttemptsList(1, 0)
                                        .addPenaltyList(14, 0)
                                        .addProblemStateList(
                                                IcpcScoreboardProblemState.ACCEPTED,
                                                IcpcScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }
        }
    }

    @Test
    void filter_contestant_jids() {
        IcpcScoreboardEntry entry = new IcpcScoreboardEntry.Builder()
                .rank(0)
                .contestantJid("123")
                .totalAccepted(5)
                .totalPenalties(12)
                .lastAcceptedPenalty(123)
                .build();

        IcpcScoreboard scoreboard = new IcpcScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3", "c4")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new IcpcScoreboardContent.Builder()
                        .addEntries(
                                new IcpcScoreboardEntry.Builder().from(entry).rank(1).contestantJid("c1").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(2).contestantJid("c2").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(3).contestantJid("c3").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(4).contestantJid("c4").build())
                        .build())
                .build();

        IcpcScoreboard filteredScoreboard = new IcpcScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c3")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new IcpcScoreboardContent.Builder()
                        .addEntries(
                                new IcpcScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c1").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c3").build())
                        .build())
                .build();

        assertThat(scoreboardProcessor.filterContestantJids(scoreboard, ImmutableSet.of("c1", "c3")))
                .isEqualTo(filteredScoreboard);
    }
}
