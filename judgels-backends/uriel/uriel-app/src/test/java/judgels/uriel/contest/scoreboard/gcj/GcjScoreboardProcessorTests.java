package judgels.uriel.contest.scoreboard.gcj;

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
import judgels.gabriel.api.Verdicts;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.ProgrammingSubmission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.GcjStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardContent;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GcjScoreboardProcessorTests {
    @Mock private ObjectMapper mapper;
    private GcjScoreboardProcessor scoreboardProcessor = new GcjScoreboardProcessor();

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
                .problemPoints(ImmutableList.of(1, 10))
                .build();

        private Contest contest = new Contest.Builder()
                .beginTime(Instant.ofEpochSecond(60))
                .duration(Duration.ofMinutes(100))
                .id(1)
                .jid("JIDC")
                .name("contest-name")
                .slug("contest-slug")
                .style(ContestStyle.GCJ)
                .build();

        private StyleModuleConfig styleModuleConfig = new GcjStyleModuleConfig.Builder()
                .wrongSubmissionPenalty(10)
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
        void show_only_contestant() throws JsonProcessingException {
            List<ProgrammingSubmission> submissions = ImmutableList.of(
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c3")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(0)
                                    .verdict(Verdicts.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build());

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                    .state(state)
                    .content(new GcjScoreboardContent.Builder()
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .totalPoints(0)
                                    .totalPenalties(0)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(0, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.NOT_ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .totalPoints(0)
                                    .totalPenalties(0)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(0, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.NOT_ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void show_only_contest_problem() throws JsonProcessingException {
            List<ProgrammingSubmission> submissions = ImmutableList.of(
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c1")
                            .problemJid("p4")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(0)
                                    .verdict(Verdicts.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build());

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                    .state(state)
                    .content(new GcjScoreboardContent.Builder()
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .totalPoints(0)
                                    .totalPenalties(0)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(0, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.NOT_ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .totalPoints(0)
                                    .totalPenalties(0)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(0, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.NOT_ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void ignore_submission_with_no_grade() throws JsonProcessingException {
            List<ProgrammingSubmission> submissions = ImmutableList.of(
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c1")
                            .problemJid("p1")
                            .build(),
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(600))
                            .userJid("c1")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(3)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(620))
                            .userJid("c2")
                            .problemJid("p2")
                            .build());

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                    .state(state)
                    .content(new GcjScoreboardContent.Builder()
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .totalPoints(1)
                                    .totalPenalties(9)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(9, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(2)
                                    .contestantJid("c2")
                                    .totalPoints(0)
                                    .totalPenalties(0)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(0, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.NOT_ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void time_calculation() throws JsonProcessingException {
            List<ProgrammingSubmission> submissions = ImmutableList.of(
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(1)
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
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(360))
                            .userJid("c1")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(3)
                                    .jid("JIDG-4")
                                    .score(0)
                                    .verdict(Verdicts.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(3)
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
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(4)
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
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(5)
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
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                    .state(state)
                    .content(new GcjScoreboardContent.Builder()
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .totalPoints(11)
                                    .totalPenalties(26)
                                    .addAttemptsList(1, 1)
                                    .addPenaltyList(4, 6)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.ACCEPTED,
                                            GcjScoreboardProblemState.ACCEPTED
                                    )
                                    .build())
                            .addEntries(new GcjScoreboardEntry.Builder()
                                    .rank(2)
                                    .contestantJid("c2")
                                    .totalPoints(1)
                                    .totalPenalties(10)
                                    .addAttemptsList(0, 0)
                                    .addPenaltyList(10, 0)
                                    .addProblemStateList(
                                            GcjScoreboardProblemState.ACCEPTED,
                                            GcjScoreboardProblemState.NOT_ACCEPTED
                                    )
                                    .build())
                            .build())
                    .build());
        }

        @Nested
        class ProblemOrdering {
            private List<ProgrammingSubmission> submissions = ImmutableList.of(
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(350))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(400))
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
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .totalPoints(10)
                                        .totalPenalties(6)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(0, 6)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.NOT_ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c2")
                                        .totalPoints(1)
                                        .totalPenalties(1)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(1, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
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
                        .problemPoints(ImmutableList.of(10, 1))
                        .build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .totalPoints(10)
                                        .totalPenalties(6)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(6, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c2")
                                        .totalPoints(1)
                                        .totalPenalties(1)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(0, 1)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.NOT_ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }
        }

        @Nested
        class Sorting {
            @Test
            void points_over_penalty() throws JsonProcessingException {
                List<ProgrammingSubmission> submissions = ImmutableList.of(
                        new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(1)
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
                        new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(2)
                                .jid("JIDS-4")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c2")
                                .problemJid("p2")
                                .latestGrading(new Grading.Builder()
                                        .id(3)
                                        .jid("JIDG-4")
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
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(10)
                                        .totalPenalties(10)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(0, 10)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.NOT_ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(4)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(4, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void penalty_as_tiebreaker() throws JsonProcessingException {
                List<ProgrammingSubmission> submissions = ImmutableList.of(
                        new ProgrammingSubmission.Builder()
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
                        new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(2)
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
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(1)
                                        .totalPenalties(10)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(14)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(14, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
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
                        .problemPoints(ImmutableList.of(1, 10))
                        .build();

                List<ProgrammingSubmission> submissions = ImmutableList.of(
                        new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(1)
                                .jid("JIDS-1")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(660))
                                .userJid("c1")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-1")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build(),
                        new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(2)
                                .jid("JIDS-2")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c2")
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
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(10)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(1)
                                        .totalPenalties(10)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(10, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(3)
                                        .contestantJid("c3")
                                        .totalPoints(0)
                                        .totalPenalties(0)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(0, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.NOT_ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }
        }

        @Nested
        class PendingAfterFreeze {
            private Optional<Instant> freezeTime = Optional.of(Instant.ofEpochSecond(500));

            private List<ProgrammingSubmission> baseSubmissions = ImmutableList.of(
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(100))
                            .userJid("c1")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(2)
                            .jid("JIDS-2")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(400))
                            .userJid("c2")
                            .problemJid("p2")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build(),
                    new ProgrammingSubmission.Builder()
                            .containerJid("JIDC")
                            .id(3)
                            .jid("JIDS-3")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(450))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-2")
                                    .score(100)
                                    .verdict(Verdicts.ACCEPTED)
                                    .build())
                            .build());

            @Test
            void no_pending() throws JsonProcessingException {
                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        baseSubmissions,
                        freezeTime);

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(11)
                                        .totalPenalties(3)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(3, 2)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(1)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(1, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void pending_does_not_overwrite_accepted() throws JsonProcessingException {
                List<ProgrammingSubmission> submissions = new ImmutableList.Builder<ProgrammingSubmission>()
                        .addAll(baseSubmissions)
                        .add(new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(4)
                                .jid("JIDS-4")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(501))
                                .userJid("c1")
                                .problemJid("p1")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build())
                        .build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions,
                        freezeTime);

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(11)
                                        .totalPenalties(3)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(3, 2)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(1)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(1, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.NOT_ACCEPTED
                                        )
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void pending_does_overwrite_not_accepted() throws JsonProcessingException {
                List<ProgrammingSubmission> submissions = new ImmutableList.Builder<ProgrammingSubmission>()
                        .addAll(baseSubmissions)
                        .add(new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(4)
                                .jid("JIDS-4")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(501))
                                .userJid("c1")
                                .problemJid("p2")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build())
                        .build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions,
                        freezeTime);

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(11)
                                        .totalPenalties(3)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(3, 2)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(1)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(1, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.FROZEN
                                        )
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void pending_counts_on_freeze_time() throws JsonProcessingException {
                List<ProgrammingSubmission> submissions = new ImmutableList.Builder<ProgrammingSubmission>()
                        .addAll(baseSubmissions)
                        .add(new ProgrammingSubmission.Builder()
                                .containerJid("JIDC")
                                .id(4)
                                .jid("JIDS-4")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(500))
                                .userJid("c1")
                                .problemJid("p2")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(100)
                                        .verdict(Verdicts.ACCEPTED)
                                        .build())
                                .build())
                        .build();

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions,
                        freezeTime);

                verify(mapper).writeValueAsString(new GcjScoreboard.Builder()
                        .state(state)
                        .content(new GcjScoreboardContent.Builder()
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .totalPoints(11)
                                        .totalPenalties(3)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(3, 2)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.ACCEPTED
                                        )
                                        .build())
                                .addEntries(new GcjScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c1")
                                        .totalPoints(1)
                                        .totalPenalties(1)
                                        .addAttemptsList(0, 0)
                                        .addPenaltyList(1, 0)
                                        .addProblemStateList(
                                                GcjScoreboardProblemState.ACCEPTED,
                                                GcjScoreboardProblemState.FROZEN
                                        )
                                        .build())
                                .build())
                        .build());
            }
        }
    }

    @Test
    void filter_contestant_jids() {
        GcjScoreboardEntry entry = new GcjScoreboardEntry.Builder()
                .rank(0)
                .contestantJid("123")
                .totalPoints(20)
                .totalPenalties(12)
                .build();

        GcjScoreboard scoreboard = new GcjScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3", "c4")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new GcjScoreboardContent.Builder()
                        .addEntries(
                                new GcjScoreboardEntry.Builder().from(entry).rank(1).contestantJid("c1").build(),
                                new GcjScoreboardEntry.Builder().from(entry).rank(2).contestantJid("c2").build(),
                                new GcjScoreboardEntry.Builder().from(entry).rank(3).contestantJid("c3").build(),
                                new GcjScoreboardEntry.Builder().from(entry).rank(4).contestantJid("c4").build())
                        .build())
                .build();

        GcjScoreboard filteredScoreboard = new GcjScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c3")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new GcjScoreboardContent.Builder()
                        .addEntries(
                                new GcjScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c1").build(),
                                new GcjScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c3").build())
                        .build())
                .build();

        assertThat(scoreboardProcessor.filterContestantJids(scoreboard, ImmutableSet.of("c1", "c3")))
                .isEqualTo(filteredScoreboard);
    }

    @Test
    void test_pagination() {
        ScoreboardState state = new ScoreboardState.Builder()
                .addContestantJids("c1", "c2")
                .addProblemJids("p1", "p2")
                .addProblemAliases("A", "B")
                .build();

        List<GcjScoreboardEntry> fakeEntries = new ArrayList<>(134);
        for (int i = 0; i < 134; i++) {
            fakeEntries.add(mock(GcjScoreboardEntry.class));
        }

        GcjScoreboard gcjScoreboard = new GcjScoreboard.Builder()
                .state(state)
                .content(new GcjScoreboardContent.Builder()
                        .entries(fakeEntries)
                        .build())
                .build();

        GcjScoreboard pagedScoreboard = (GcjScoreboard) scoreboardProcessor.paginate(gcjScoreboard, 1, 50);
        System.out.println(pagedScoreboard);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(0, 50));

        pagedScoreboard = (GcjScoreboard) scoreboardProcessor.paginate(gcjScoreboard, 2, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(50, 100));

        pagedScoreboard = (GcjScoreboard) scoreboardProcessor.paginate(gcjScoreboard, 3, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(100, 134));
    }
}
