package judgels.uriel.contest.scoreboard.gcj;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.GcjStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GcjScoreboardProcessorTests {
    private GcjScoreboardProcessor scoreboardProcessor = new GcjScoreboardProcessor();

    @Nested
    class ComputeEntries {
        private ScoreboardState state = new ScoreboardState.Builder()
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

        private Set<ContestContestant> contestants = ImmutableSet.of(
                new ContestContestant.Builder().userJid("c1").build(),
                new ContestContestant.Builder().userJid("c2").contestStartTime(Instant.ofEpochSecond(300)).build());

        @Test
        void show_only_contestant() {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
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
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build());

            List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModuleConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new GcjScoreboardEntry.Builder()
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
                            .build(),
                    new GcjScoreboardEntry.Builder()
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
                            .build());
        }

        @Test
        void show_only_contest_problem() {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
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
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build());

            List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModuleConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new GcjScoreboardEntry.Builder()
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
                            .build(),
                    new GcjScoreboardEntry.Builder()
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
                            .build());
        }

        @Test
        void ignore_submission_with_no_grade() {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(1)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c1")
                            .problemJid("p1")
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
                            .containerJid("JIDC")
                            .id(3)
                            .jid("JIDS-1")
                            .gradingEngine("ENG")
                            .gradingLanguage("ASM")
                            .time(Instant.ofEpochSecond(620))
                            .userJid("c2")
                            .problemJid("p2")
                            .build());

            List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModuleConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new GcjScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c1")
                            .totalPoints(1)
                            .totalPenalties(9)
                            .addAttemptsList(1, 0)
                            .addPenaltyList(9, 0)
                            .addProblemStateList(
                                    GcjScoreboardProblemState.ACCEPTED,
                                    GcjScoreboardProblemState.NOT_ACCEPTED
                            )
                            .build(),
                    new GcjScoreboardEntry.Builder()
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
                            .build());
        }

        @Test
        void time_calculation() {
            List<Submission> submissions = ImmutableList.of(
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.TIME_LIMIT_EXCEEDED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build()
            );

            List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModuleConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new GcjScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c1")
                            .totalPoints(11)
                            .totalPenalties(26)
                            .addAttemptsList(2, 2)
                            .addPenaltyList(4, 6)
                            .addProblemStateList(
                                    GcjScoreboardProblemState.ACCEPTED,
                                    GcjScoreboardProblemState.ACCEPTED
                            )
                            .build(),
                    new GcjScoreboardEntry.Builder()
                            .rank(2)
                            .contestantJid("c2")
                            .totalPoints(1)
                            .totalPenalties(10)
                            .addAttemptsList(1, 0)
                            .addPenaltyList(10, 0)
                            .addProblemStateList(
                                    GcjScoreboardProblemState.ACCEPTED,
                                    GcjScoreboardProblemState.NOT_ACCEPTED
                            )
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
                            .time(Instant.ofEpochSecond(350))
                            .userJid("c2")
                            .problemJid("p1")
                            .latestGrading(new Grading.Builder()
                                    .id(1)
                                    .jid("JIDG-1")
                                    .score(100)
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build()
            );

            @Test
            void base_case() {
                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .totalPoints(10)
                                .totalPenalties(6)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 6)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.NOT_ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c2")
                                .totalPoints(1)
                                .totalPenalties(1)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void reversed_case() {
                state = new ScoreboardState.Builder()
                        .addProblemJids("p2", "p1")
                        .addProblemAliases("B", "A")
                        .problemPoints(ImmutableList.of(10, 1))
                        .build();

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .totalPoints(10)
                                .totalPenalties(6)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(6, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c2")
                                .totalPoints(1)
                                .totalPenalties(1)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 1)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.NOT_ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build());
            }
        }

        @Nested
        class Sorting {
            @Test
            void points_over_penalty() {
                List<Submission> submissions = ImmutableList.of(
                        new Submission.Builder()
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
                                        .verdict(Verdict.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
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
                                        .verdict(Verdict.ACCEPTED)
                                        .build())
                                .build()
                );

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(10)
                                .totalPenalties(10)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 10)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.NOT_ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(4)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(4, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void penalty_as_tiebreaker() {
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
                                        .verdict(Verdict.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
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
                                        .verdict(Verdict.ACCEPTED)
                                        .build())
                                .build()
                );

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(1)
                                .totalPenalties(10)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(14)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(14, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void same_rank_if_equal() {
                contestants = ImmutableSet.of(
                        new ContestContestant.Builder().userJid("c1").build(),
                        new ContestContestant.Builder()
                                .userJid("c2")
                                .contestStartTime(Instant.ofEpochSecond(300))
                                .build(),
                        new ContestContestant.Builder().userJid("c3").build());

                List<Submission> submissions = ImmutableList.of(
                        new Submission.Builder()
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
                                        .verdict(Verdict.ACCEPTED)
                                        .build())
                                .build(),
                        new Submission.Builder()
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
                                        .verdict(Verdict.ACCEPTED)
                                        .build())
                                .build()
                );

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(10)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(1)
                                .totalPenalties(10)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
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
                                .build());
            }

            @Test
            void zero_points_ordering() {
                contestants = ImmutableSet.of(
                        new ContestContestant.Builder().userJid("c1").build(),
                        new ContestContestant.Builder().userJid("c2").build(),
                        new ContestContestant.Builder().userJid("c3").build());

                List<Submission> submissions = ImmutableList.of(
                        new Submission.Builder()
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
                                        .score(0)
                                        .verdict(Verdict.WRONG_ANSWER)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(2)
                                .jid("JIDS-2")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c1")
                                .problemJid("p2")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(0)
                                        .verdict(Verdict.WRONG_ANSWER)
                                        .build())
                                .build(),
                        new Submission.Builder()
                                .containerJid("JIDC")
                                .id(3)
                                .jid("JIDS-3")
                                .gradingEngine("ENG")
                                .gradingLanguage("ASM")
                                .time(Instant.ofEpochSecond(900))
                                .userJid("c3")
                                .problemJid("p2")
                                .latestGrading(new Grading.Builder()
                                        .id(1)
                                        .jid("JIDG-2")
                                        .score(0)
                                        .verdict(Verdict.WRONG_ANSWER)
                                        .build())
                                .build()
                );

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .totalPoints(0)
                                .totalPenalties(0)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(0, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.NOT_ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c3")
                                .totalPoints(0)
                                .totalPenalties(0)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.NOT_ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
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
                                .build());
            }
        }

        @Nested
        class PendingAfterFreeze {
            private Optional<Instant> freezeTime = Optional.of(Instant.ofEpochSecond(500));

            private List<Submission> baseSubmissions = ImmutableList.of(
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build(),
                    new Submission.Builder()
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
                                    .verdict(Verdict.ACCEPTED)
                                    .build())
                            .build());

            @Test
            void no_pending() {
                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        baseSubmissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(11)
                                .totalPenalties(3)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(1)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void pending_does_not_overwrite_accepted() {
                List<Submission> submissions = new ImmutableList.Builder<Submission>()
                        .addAll(baseSubmissions)
                        .add(new Submission.Builder()
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
                                        .verdict(Verdict.PENDING)
                                        .build())
                                .build())
                        .build();

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(11)
                                .totalPenalties(3)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(1)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void pending_does_overwrite_not_accepted() {
                List<Submission> submissions = new ImmutableList.Builder<Submission>()
                        .addAll(baseSubmissions)
                        .add(new Submission.Builder()
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
                                        .verdict(Verdict.PENDING)
                                        .build())
                                .build())
                        .build();

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(11)
                                .totalPenalties(3)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(1)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.FROZEN
                                )
                                .build());
            }

            @Test
            void pending_counts_on_freeze_time() {
                List<Submission> submissions = new ImmutableList.Builder<Submission>()
                        .addAll(baseSubmissions)
                        .add(new Submission.Builder()
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
                                        .verdict(Verdict.PENDING)
                                        .build())
                                .build())
                        .build();

                List<GcjScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModuleConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(entries).containsExactly(
                        new GcjScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .totalPoints(11)
                                .totalPenalties(3)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.ACCEPTED
                                )
                                .build(),
                        new GcjScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .totalPoints(1)
                                .totalPenalties(1)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        GcjScoreboardProblemState.ACCEPTED,
                                        GcjScoreboardProblemState.FROZEN
                                )
                                .build());
            }
        }
    }
}
