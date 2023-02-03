package judgels.uriel.contest.scoreboard.bundle;

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
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.BundleStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BundleScoreboardProcessorTests {
    private BundleScoreboardProcessor scoreboardProcessor = new BundleScoreboardProcessor();

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
                .style(ContestStyle.BUNDLE)
                .build();

        private StyleModuleConfig styleModuleConfig = new BundleStyleModuleConfig.Builder().build();

        private Set<ContestContestant> contestants = ImmutableSet.of(
                new ContestContestant.Builder().userJid("c1").build(),
                new ContestContestant.Builder().userJid("c2").build());

        private Map<String, Profile> profilesMap = ImmutableMap.of(
                "c1", new Profile.Builder().username("c1").build(),
                "c2", new Profile.Builder().username("c2").build());

        @Test
        void latest_answered_time_calculation() {
            List<ItemSubmission> submissions = ImmutableList.of(
                    new ItemSubmission.Builder()
                            .containerJid("JIDC")
                            .jid("JIDS-1")
                            .itemJid("JIDITEM-1")
                            .answer("d")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c1")
                            .problemJid("p2")
                            .grading(new Grading.Builder()
                                    .verdict(Verdict.ACCEPTED)
                                    .score(4)
                                    .build())
                            .build(),
                    new ItemSubmission.Builder()
                            .containerJid("JIDC")
                            .jid("JIDS-5")
                            .itemJid("JIDITEM-1")
                            .answer("b")
                            .time(Instant.ofEpochSecond(501))
                            .userJid("c2")
                            .problemJid("p1")
                            .grading(new Grading.Builder()
                                    .verdict(Verdict.WRONG_ANSWER)
                                    .score(-1)
                                    .build())
                            .build(),
                    new ItemSubmission.Builder()
                            .containerJid("JIDC")
                            .jid("JIDS-3")
                            .itemJid("JIDITEM-2")
                            .answer("x")
                            .time(Instant.ofEpochSecond(500))
                            .userJid("c1")
                            .problemJid("p2")
                            .grading(new Grading.Builder()
                                    .verdict(Verdict.ACCEPTED)
                                    .score(4)
                                    .build())
                            .build(),
                    new ItemSubmission.Builder()
                            .containerJid("JIDC")
                            .jid("JIDS-4")
                            .itemJid("JIDITEM-2")
                            .answer("a")
                            .time(Instant.ofEpochSecond(600))
                            .userJid("c1")
                            .problemJid("p1")
                            .grading(new Grading.Builder()
                                    .verdict(Verdict.WRONG_ANSWER)
                                    .score(-1)
                                    .build())
                            .build()
            );

            ScoreboardProcessResult result = scoreboardProcessor.process(
                    contest,
                    state,
                    Optional.empty(),
                    styleModuleConfig,
                    contestants,
                    profilesMap,
                    ImmutableList.of(),
                    submissions,
                    Optional.empty());

            assertThat(Lists.transform(result.getEntries(), e -> (BundleScoreboardEntry) e)).containsExactly(
                    new BundleScoreboardEntry.Builder()
                            .rank(1)
                            .contestantJid("c1")
                            .answeredItems(ImmutableList.of(1, 2))
                            .totalAnsweredItems(3)
                            .lastAnsweredTime(Instant.ofEpochSecond(600))
                            .build(),
                    new BundleScoreboardEntry.Builder()
                            .rank(2)
                            .contestantJid("c2")
                            .answeredItems(ImmutableList.of(1, 0))
                            .totalAnsweredItems(1)
                            .lastAnsweredTime(Instant.ofEpochSecond(501))
                            .build());
        }

        @Nested
        class Sorting {

            @Test
            void total_answered_items_over_last_answered_time() {
                List<ItemSubmission> submissions = ImmutableList.of(
                        new ItemSubmission.Builder()
                                .containerJid("JIDC")
                                .jid("JIDS-1")
                                .itemJid("JIDITEM-1")
                                .answer("d")
                                .time(Instant.ofEpochSecond(300))
                                .userJid("c1")
                                .problemJid("p1")
                                .grading(new Grading.Builder()
                                        .verdict(Verdict.ACCEPTED)
                                        .score(4)
                                        .build())
                                .build(),
                        new ItemSubmission.Builder()
                                .containerJid("JIDC")
                                .jid("JIDS-5")
                                .itemJid("JIDITEM-1")
                                .answer("b")
                                .time(Instant.ofEpochSecond(400))
                                .userJid("c2")
                                .problemJid("p1")
                                .grading(new Grading.Builder()
                                        .verdict(Verdict.WRONG_ANSWER)
                                        .score(-1)
                                        .build())
                                .build(),
                        new ItemSubmission.Builder()
                                .containerJid("JIDC")
                                .jid("JIDS-3")
                                .itemJid("JIDITEM-2")
                                .answer("x")
                                .time(Instant.ofEpochSecond(100))
                                .userJid("c1")
                                .problemJid("p2")
                                .grading(new Grading.Builder()
                                        .verdict(Verdict.ACCEPTED)
                                        .score(4)
                                        .build())
                                .build()
                );

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        ImmutableList.of(),
                        submissions,
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (BundleScoreboardEntry) e)).containsExactly(
                        new BundleScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .answeredItems(ImmutableList.of(1, 1))
                                .totalAnsweredItems(2)
                                .lastAnsweredTime(Instant.ofEpochSecond(300))
                                .build(),
                        new BundleScoreboardEntry.Builder()
                                .rank(2)
                                .contestantJid("c2")
                                .answeredItems(ImmutableList.of(1, 0))
                                .totalAnsweredItems(1)
                                .lastAnsweredTime(Instant.ofEpochSecond(400))
                                .build());
            }

            @Test
            void last_answered_time_as_tiebreaker() {
                List<ItemSubmission> submissions = ImmutableList.of(
                        new ItemSubmission.Builder()
                                .containerJid("JIDC")
                                .jid("JIDS-1")
                                .itemJid("JIDITEM-1")
                                .answer("d")
                                .time(Instant.ofEpochSecond(300))
                                .userJid("c1")
                                .problemJid("p1")
                                .grading(new Grading.Builder()
                                        .verdict(Verdict.ACCEPTED)
                                        .score(4)
                                        .build())
                                .build(),
                        new ItemSubmission.Builder()
                                .containerJid("JIDC")
                                .jid("JIDS-5")
                                .itemJid("JIDITEM-1")
                                .answer("b")
                                .time(Instant.ofEpochSecond(400))
                                .userJid("c2")
                                .problemJid("p1")
                                .grading(new Grading.Builder()
                                        .verdict(Verdict.WRONG_ANSWER)
                                        .score(-1)
                                        .build())
                                .build()
                );

                ScoreboardProcessResult result = scoreboardProcessor.process(
                        contest,
                        state,
                        Optional.empty(),
                        styleModuleConfig,
                        contestants,
                        profilesMap,
                        ImmutableList.of(),
                        submissions,
                        Optional.empty());

                assertThat(Lists.transform(result.getEntries(), e -> (BundleScoreboardEntry) e)).containsExactly(
                        new BundleScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c2")
                                .answeredItems(ImmutableList.of(1, 0))
                                .totalAnsweredItems(1)
                                .lastAnsweredTime(Instant.ofEpochSecond(400))
                                .build(),
                        new BundleScoreboardEntry.Builder()
                                .rank(1)
                                .contestantJid("c1")
                                .answeredItems(ImmutableList.of(1, 0))
                                .totalAnsweredItems(1)
                                .lastAnsweredTime(Instant.ofEpochSecond(300))
                                .build());
            }
        }
    }
}
