package judgels.uriel.contest.scoreboard.icpc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.Verdict;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.AbstractProgrammingScoreboardProcessorTests;
import judgels.uriel.contest.scoreboard.ScoreboardProcessResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class IcpcScoreboardProcessorTests extends AbstractProgrammingScoreboardProcessorTests {
    private IcpcScoreboardProcessor scoreboardProcessor = new IcpcScoreboardProcessor();

    @Nested
    class Process {
        private ScoreboardState state = new ScoreboardState.Builder()
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

        private Set<ContestContestant> contestants = ImmutableSet.of(
                new ContestContestant.Builder().userJid("c1").build(),
                new ContestContestant.Builder().userJid("c2").contestStartTime(Instant.ofEpochSecond(300)).build());

        private Map<String, Profile> profilesMap = ImmutableMap.of(
                "c1", new Profile.Builder().username("c1").build(),
                "c2", new Profile.Builder().username("c2").build());

        @Test
        void time_calculation() {
            List<Submission> submissions = ImmutableList.of(
                    createSubmission(1, 300, "c1", "p1", 100, Verdict.ACCEPTED),
                    createSubmission(2, 360, "c2", "p2", 100, Verdict.ACCEPTED),
                    createSubmission(3, 400, "c1", "p2", 0, Verdict.TIME_LIMIT_EXCEEDED),
                    createSubmission(4, 410, "c1", "p2", 100, Verdict.ACCEPTED),
                    createSubmission(5, 900, "c2", "p1", 100, Verdict.ACCEPTED));

            ScoreboardProcessResult result = scoreboardProcessor.process(
                    contest,
                    state,
                    Optional.empty(),
                    styleModuleConfig,
                    contestants,
                    profilesMap,
                    submissions,
                    ImmutableList.of(),
                    Optional.empty());

            assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                    new IcpcScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c2")
                            .contestantUsername("c2")
                            .contestantRating(0)
                            .totalAccepted(2)
                            .totalPenalties(11)
                            .lastAcceptedPenalty(600000)
                            .addAttemptsList(1, 1)
                            .addPenaltyList(10, 1)
                            .addProblemStateList(
                                    IcpcScoreboardProblemState.ACCEPTED,
                                    IcpcScoreboardProblemState.FIRST_ACCEPTED
                            )
                            .build(),
                    new IcpcScoreboardEntry.Builder()
                            .rank(2)
                            .contestantJid("c1")
                            .contestantUsername("c1")
                            .contestantRating(0)
                            .totalAccepted(2)
                            .totalPenalties(1010)
                            .lastAcceptedPenalty(350000)
                            .addAttemptsList(1, 2)
                            .addPenaltyList(4, 6)
                            .addProblemStateList(
                                    IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                    IcpcScoreboardProblemState.ACCEPTED
                            )
                            .build());
        }

        @Nested
        class ProblemOrdering {
            private List<Submission> submissions = ImmutableList.of(
                    createSubmission(1, 900, "c2", "p1", 100, Verdict.ACCEPTED),
                    createSubmission(2, 300, "c1", "p2", 100, Verdict.ACCEPTED));

            @Test
            void base_case() {
                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(4)
                                .lastAcceptedPenalty(240000)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 4)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.NOT_ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(10)
                                .lastAcceptedPenalty(600000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void reversed_case() {
                state = new ScoreboardState.Builder()
                        .addProblemJids("p2", "p1")
                        .addProblemAliases("B", "A")
                        .build();

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(4)
                                .lastAcceptedPenalty(240000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(4, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(10)
                                .lastAcceptedPenalty(600000)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 10)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.NOT_ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build());
            }
        }

        @Nested
        class Sorting {
            @Test
            void solve_over_penalty() {
                List<Submission> submissions = ImmutableList.of(
                        createSubmission(1, 300, "c1", "p1", 100, Verdict.ACCEPTED),
                        createSubmission(2, 360, "c2", "p2", 100, Verdict.ACCEPTED),
                        createSubmission(3, 900, "c2", "p1", 100, Verdict.ACCEPTED));

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(2)
                                .totalPenalties(11)
                                .lastAcceptedPenalty(600000)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(10, 1)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(4)
                                .lastAcceptedPenalty(240000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(4, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void penalty_as_tiebreaker() {
                List<Submission> submissions = ImmutableList.of(
                        createSubmission(1, 900, "c2", "p1", 100, Verdict.ACCEPTED),
                        createSubmission(2, 900, "c1", "p1", 100, Verdict.ACCEPTED));

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(10)
                                .lastAcceptedPenalty(600000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(14)
                                .lastAcceptedPenalty(840000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(14, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
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

                profilesMap = ImmutableMap.of(
                        "c1", new Profile.Builder().username("c1").build(),
                        "c2", new Profile.Builder().username("c2").build(),
                        "c3", new Profile.Builder().username("c3").build());

                List<Submission> submissions = ImmutableList.of(
                        createSubmission(1, 660, "c1", "p1", 100, Verdict.ACCEPTED),
                        createSubmission(2, 900, "c2", "p1", 100, Verdict.ACCEPTED),
                        createSubmission(3, 900, "c3", "p1", 100, Verdict.ACCEPTED));

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(10)
                                .lastAcceptedPenalty(600000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(10)
                                .lastAcceptedPenalty(600000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(10, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(3)
                                .contestantJid("c3")
                                .contestantUsername("c3")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(14)
                                .lastAcceptedPenalty(840000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(14, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void zero_points_ordering() {
                contestants = ImmutableSet.of(
                        new ContestContestant.Builder().userJid("c1").build(),
                        new ContestContestant.Builder().userJid("c2").build(),
                        new ContestContestant.Builder().userJid("c3").build());

                profilesMap = ImmutableMap.of(
                        "c1", new Profile.Builder().username("c1").build(),
                        "c2", new Profile.Builder().username("c2").build(),
                        "c3", new Profile.Builder().username("c3").build());

                List<Submission> submissions = ImmutableList.of(
                        createSubmission(1, 660, "c1", "p1", 0, Verdict.WRONG_ANSWER),
                        createSubmission(2, 900, "c1", "p2", 0, Verdict.WRONG_ANSWER),
                        createSubmission(3, 900, "c3", "p2", 0, Verdict.WRONG_ANSWER));

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(0)
                                .totalPenalties(0)
                                .lastAcceptedPenalty(0)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(0, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.NOT_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c3")
                                .contestantUsername("c3")
                                .contestantRating(0)
                                .totalAccepted(0)
                                .totalPenalties(0)
                                .lastAcceptedPenalty(0)
                                .addAttemptsList(0, 1)
                                .addPenaltyList(0, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.NOT_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(0)
                                .totalPenalties(0)
                                .lastAcceptedPenalty(0)
                                .addAttemptsList(0, 0)
                                .addPenaltyList(0, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.NOT_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }
        }

        @Nested
        class PendingAfterFreeze {
            private Optional<Instant> freezeTime = Optional.of(Instant.ofEpochSecond(500));

            private List<Submission> baseSubmissions = ImmutableList.of(
                    createSubmission(1, 100, "c1", "p1", 100, Verdict.ACCEPTED),
                    createSubmission(2, 400, "c2", "p2", 100, Verdict.ACCEPTED),
                    createSubmission(3, 450, "c2", "p1", 100, Verdict.ACCEPTED));

            @Test
            void no_pending() {
                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        baseSubmissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(2)
                                .totalPenalties(5)
                                .lastAcceptedPenalty(150000)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(1)
                                .lastAcceptedPenalty(40000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void pending_does_not_overwrite_accepted() {
                List<Submission> submissions = new ImmutableList.Builder<Submission>()
                        .addAll(baseSubmissions)
                        .add(createSubmission(4, 501, "c1", "p1", 100, Verdict.ACCEPTED))
                        .build();

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(2)
                                .totalPenalties(5)
                                .lastAcceptedPenalty(150000)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(1)
                                .lastAcceptedPenalty(40000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.NOT_ACCEPTED
                                )
                                .build());
            }

            @Test
            void pending_does_overwrite_not_accepted() {
                List<Submission> submissions = new ImmutableList.Builder<Submission>()
                        .addAll(baseSubmissions)
                        .add(createSubmission(4, 501, "c1", "p2", 100, Verdict.ACCEPTED))
                        .build();

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(2)
                                .totalPenalties(5)
                                .lastAcceptedPenalty(150000)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(1)
                                .lastAcceptedPenalty(40000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.FROZEN
                                )
                                .build());
            }

            @Test
            void pending_counts_on_freeze_time() {
                List<Submission> submissions = new ImmutableList.Builder<Submission>()
                        .addAll(baseSubmissions)
                        .add(createSubmission(4, 500, "c1", "p2", 100, Verdict.ACCEPTED))
                        .build();

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        freezeTime);

                assertThat(Lists.transform(result.getEntries(), e -> (IcpcScoreboardEntry) e)).containsExactly(
                        new IcpcScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .contestantUsername("c2")
                                .contestantRating(0)
                                .totalAccepted(2)
                                .totalPenalties(5)
                                .lastAcceptedPenalty(150000)
                                .addAttemptsList(1, 1)
                                .addPenaltyList(3, 2)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.ACCEPTED,
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED
                                )
                                .build(),
                        new IcpcScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c1")
                                .contestantUsername("c1")
                                .contestantRating(0)
                                .totalAccepted(1)
                                .totalPenalties(1)
                                .lastAcceptedPenalty(40000)
                                .addAttemptsList(1, 0)
                                .addPenaltyList(1, 0)
                                .addProblemStateList(
                                        IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                        IcpcScoreboardProblemState.FROZEN
                                )
                                .build());
            }
        }

        @Nested
        class IncrementalProcess {
            List<Submission> submissions = ImmutableList.of(
                    createSubmission(5, 100, "c1", "p1", 0, Verdict.WRONG_ANSWER),
                    createSubmission(6, 200, "c2", "p1", 0, Verdict.WRONG_ANSWER),
                    createSubmission(7, 300, "c1", "p1", 0, Verdict.WRONG_ANSWER),
                    createSubmission(8, 400, "c1", "p1", 100, Verdict.ACCEPTED),
                    createSubmission(9, 500, "c2", "p2", 100, Verdict.ACCEPTED),
                    createSubmission(10, 600, "c2", "p1", 0, Verdict.PENDING),
                    createSubmission(11, 700, "c1", "p2", 100, Verdict.ACCEPTED));

            Set<ContestContestant> contestants = ImmutableSet.of(
                    new ContestContestant.Builder().userJid("c1").build(),
                    new ContestContestant.Builder().userJid("c2").contestStartTime(Instant.ofEpochSecond(300)).build(),
                    new ContestContestant.Builder().userJid("c3").build());

            Map<String, Profile> profilesMap = ImmutableMap.of(
                    "c1", new Profile.Builder().username("c1").build(),
                    "c2", new Profile.Builder().username("c2").build(),
                    "c3", new Profile.Builder().username("c3").build());

            IcpcScoreboardIncrementalContent incrementalContent = new IcpcScoreboardIncrementalContent.Builder()
                    .lastSubmissionId(3)
                    .putFirstToSolveSubmissionJids("p1", "JIDSX")
                    .putLastAcceptedPenaltiesByContestantJid("c2", 0)
                    .putLastAcceptedPenaltiesByContestantJid("c3", 90000)
                    .putAttemptsMapsByContestantJid("c2", ImmutableMap.of("p1", 0, "p2", 2))
                    .putAttemptsMapsByContestantJid("c3", ImmutableMap.of("p1", 1, "p2", 0))
                    .putPenaltyMapsByContestantJid("c2", ImmutableMap.of("p1", 0L, "p2", 3L))
                    .putPenaltyMapsByContestantJid("c3", ImmutableMap.of("p1", 3L, "p2", 0L))
                    .putProblemStateMapsByContestantJid("c2", ImmutableMap.of(
                            "p1", IcpcScoreboardProblemState.NOT_ACCEPTED,
                            "p2", IcpcScoreboardProblemState.NOT_ACCEPTED))
                    .putProblemStateMapsByContestantJid("c3", ImmutableMap.of(
                            "p1", IcpcScoreboardProblemState.ACCEPTED,
                            "p2", IcpcScoreboardProblemState.NOT_ACCEPTED))
                    .build();

            @Test
            void empty_initial_incremental_content() {
                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(result.getIncrementalContent()).isEqualTo(new IcpcScoreboardIncrementalContent.Builder()
                        .lastSubmissionId(9)
                        .putFirstToSolveSubmissionJids("p1", "JIDS8")
                        .putFirstToSolveSubmissionJids("p2", "JIDS9")
                        .putLastAcceptedPenaltiesByContestantJid("c1", 340000L)
                        .putLastAcceptedPenaltiesByContestantJid("c2", 200000L)
                        .putAttemptsMapsByContestantJid("c1", ImmutableMap.of("p1", 3, "p2", 0))
                        .putAttemptsMapsByContestantJid("c2", ImmutableMap.of("p1", 1, "p2", 1))
                        .putPenaltyMapsByContestantJid("c1", ImmutableMap.of("p1", 6L, "p2", 0L))
                        .putPenaltyMapsByContestantJid("c2", ImmutableMap.of("p1", 0L, "p2", 4L))
                        .putProblemStateMapsByContestantJid("c1", ImmutableMap.of(
                                "p1", IcpcScoreboardProblemState.FIRST_ACCEPTED,
                                "p2", IcpcScoreboardProblemState.NOT_ACCEPTED))
                        .putProblemStateMapsByContestantJid("c2", ImmutableMap.of(
                                "p1", IcpcScoreboardProblemState.NOT_ACCEPTED,
                                "p2", IcpcScoreboardProblemState.FIRST_ACCEPTED))
                        .build());
            }

            @Test
            void empty_new_submissions() {
                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.of(incrementalContent),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        ImmutableList.of(),
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(result.getIncrementalContent()).isEqualTo(new IcpcScoreboardIncrementalContent.Builder()
                        .from(incrementalContent)
                        .lastSubmissionId(3)
                        .build());
            }

            @Test
            void existing_incremental_content() {
                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.of(incrementalContent),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        submissions,
                        ImmutableList.of(),
                        Optional.empty());

                assertThat(result.getIncrementalContent()).isEqualTo(new IcpcScoreboardIncrementalContent.Builder()
                        .lastSubmissionId(9)
                        .putFirstToSolveSubmissionJids("p1", "JIDSX")
                        .putFirstToSolveSubmissionJids("p2", "JIDS9")
                        .putLastAcceptedPenaltiesByContestantJid("c1", 340000L)
                        .putLastAcceptedPenaltiesByContestantJid("c2", 200000L)
                        .putLastAcceptedPenaltiesByContestantJid("c3", 90000L)
                        .putAttemptsMapsByContestantJid("c1", ImmutableMap.of("p1", 3, "p2", 0))
                        .putAttemptsMapsByContestantJid("c2", ImmutableMap.of("p1", 1, "p2", 3))
                        .putAttemptsMapsByContestantJid("c3", ImmutableMap.of("p1", 1, "p2", 0))
                        .putPenaltyMapsByContestantJid("c1", ImmutableMap.of("p1", 6L, "p2", 0L))
                        .putPenaltyMapsByContestantJid("c2", ImmutableMap.of("p1", 0L, "p2", 4L))
                        .putPenaltyMapsByContestantJid("c3", ImmutableMap.of("p1", 3L, "p2", 0L))
                        .putProblemStateMapsByContestantJid("c1", ImmutableMap.of(
                                "p1", IcpcScoreboardProblemState.ACCEPTED,
                                "p2", IcpcScoreboardProblemState.NOT_ACCEPTED))
                        .putProblemStateMapsByContestantJid("c2", ImmutableMap.of(
                                "p1", IcpcScoreboardProblemState.NOT_ACCEPTED,
                                "p2", IcpcScoreboardProblemState.FIRST_ACCEPTED))
                        .putProblemStateMapsByContestantJid("c3", ImmutableMap.of(
                                "p1", IcpcScoreboardProblemState.ACCEPTED,
                                "p2", IcpcScoreboardProblemState.NOT_ACCEPTED))
                        .build());
            }
        }
    }
}
