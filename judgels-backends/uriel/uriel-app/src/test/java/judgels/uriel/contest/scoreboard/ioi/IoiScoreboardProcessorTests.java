package judgels.uriel.contest.scoreboard.ioi;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class IoiScoreboardProcessorTests {
    private IoiScoreboardProcessor scoreboardProcessor = new IoiScoreboardProcessor();

    @Nested
    class ComputeEntries {
        private ScoreboardState state = new ScoreboardState.Builder()
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

        private Set<ContestContestant> contestants = ImmutableSet.of(
                new ContestContestant.Builder().userJid("c1").build(),
                new ContestContestant.Builder().userJid("c2").contestStartTime(Instant.ofEpochMilli(10)).build());

        @Test
        void only_count_contestant() {
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

            List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModulesConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new IoiScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c1")
                            .addScores(
                                    Optional.empty(),
                                    Optional.empty()
                            )
                            .totalScores(0)
                            .lastAffectingPenalty(0)
                            .build(),
                    new IoiScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c2")
                            .addScores(
                                    Optional.empty(),
                                    Optional.empty()
                            )
                            .totalScores(0)
                            .lastAffectingPenalty(0)
                            .build());
        }

        @Test
        void ignore_other_problem() {
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

            List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModulesConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new IoiScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c1")
                            .addScores(
                                    Optional.empty(),
                                    Optional.empty()
                            )
                            .totalScores(0)
                            .lastAffectingPenalty(0)
                            .build(),
                    new IoiScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c2")
                            .addScores(
                                    Optional.empty(),
                                    Optional.empty()
                            )
                            .totalScores(0)
                            .lastAffectingPenalty(0)
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

            List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                    state,
                    contest,
                    styleModulesConfig,
                    contestants,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(entries).containsExactly(
                    new IoiScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c2")
                            .addScores(
                                    Optional.of(78),
                                    Optional.empty()
                            )
                            .totalScores(78)
                            .lastAffectingPenalty(10)
                            .build(),
                    new IoiScoreboardEntry.Builder()
                            .rank(2)
                            .contestantJid("c1")
                            .addScores(
                                    Optional.of(0),
                                    Optional.of(50)
                            )
                            .totalScores(50)
                            .lastAffectingPenalty(15)
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
            void base_case() {
                List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModulesConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .addScores(
                                        Optional.of(50),
                                        Optional.empty()
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(10)
                                .build(),
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .addScores(
                                        Optional.empty(),
                                        Optional.of(50)
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(15)
                                .build());
            }

            @Test
            void reversed_case() {
                state = new ScoreboardState.Builder()
                        .addProblemJids("p2", "p1")
                        .addProblemAliases("B", "A")
                        .build();

                List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModulesConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .addScores(
                                        Optional.empty(),
                                        Optional.of(50)
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(10)
                                .build(),
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .addScores(
                                        Optional.of(50),
                                        Optional.empty()
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(15)
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
            void sorted_without_last_affecting_penalty() {
                styleModulesConfig = new IoiStyleModuleConfig.Builder().usingLastAffectingPenalty(false).build();

                List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModulesConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .addScores(
                                        Optional.of(50),
                                        Optional.empty()
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(10)
                                .build(),
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .addScores(
                                        Optional.empty(),
                                        Optional.of(50)
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(15)
                                .build());
            }

            @Test
            void sorted_with_last_affecting_penalty() {
                styleModulesConfig = new IoiStyleModuleConfig.Builder().usingLastAffectingPenalty(true).build();

                List<IoiScoreboardEntry> entries = scoreboardProcessor.computeEntries(
                        state,
                        contest,
                        styleModulesConfig,
                        contestants,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(entries).containsExactly(
                        new IoiScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .addScores(
                                        Optional.of(50),
                                        Optional.empty()
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(10)
                                .build(),
                        new IoiScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .addScores(
                                        Optional.empty(),
                                        Optional.of(50)
                                )
                                .totalScores(50)
                                .lastAffectingPenalty(15)
                                .build());
            }
        }
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
