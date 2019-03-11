package judgels.uriel.contest.scoreboard.bundle;

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
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.BundleStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardContent;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class BundleScoreboardProcessorTests {
    @Mock private ObjectMapper mapper;
    private BundleScoreboardProcessor scoreboardProcessor = new BundleScoreboardProcessor();

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
                .style(ContestStyle.BUNDLE)
                .build();

        private StyleModuleConfig styleModuleConfig = new BundleStyleModuleConfig.Builder().build();

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
            List<ItemSubmission> submissions = ImmutableList.of(
                    new ItemSubmission.Builder()
                            .containerJid("JIDC")
                            .jid("JIDS-1")
                            .itemJid("JIDITEM-1")
                            .answer("d")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c3")
                            .problemJid("p1")
                            .grading(new Grading.Builder()
                                    .verdict(Verdict.WRONG_ANSWER)
                                    .score(-1)
                                    .build())
                            .build());

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    ImmutableList.of(),
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new BundleScoreboard.Builder()
                    .state(state)
                    .content(new BundleScoreboardContent.Builder()
                            .addEntries(new BundleScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .answeredItems(ImmutableList.of(0, 0))
                                    .totalAnsweredItems(0)
                                    .lastAnsweredTime(Optional.empty())
                                    .build())
                            .addEntries(new BundleScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .answeredItems(ImmutableList.of(0, 0))
                                    .totalAnsweredItems(0)
                                    .lastAnsweredTime(Optional.empty())
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void show_only_contest_problem() throws JsonProcessingException {
            List<ItemSubmission> submissions = ImmutableList.of(
                    new ItemSubmission.Builder()
                            .containerJid("JIDC")
                            .jid("JIDS-1")
                            .itemJid("JIDITEM-1")
                            .answer("d")
                            .time(Instant.ofEpochSecond(300))
                            .userJid("c2")
                            .problemJid("p4")
                            .grading(new Grading.Builder()
                                    .verdict(Verdict.WRONG_ANSWER)
                                    .score(-1)
                                    .build())
                            .build());

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    ImmutableList.of(),
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new BundleScoreboard.Builder()
                    .state(state)
                    .content(new BundleScoreboardContent.Builder()
                            .addEntries(new BundleScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .answeredItems(ImmutableList.of(0, 0))
                                    .totalAnsweredItems(0)
                                    .lastAnsweredTime(Optional.empty())
                                    .build())
                            .addEntries(new BundleScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c2")
                                    .answeredItems(ImmutableList.of(0, 0))
                                    .totalAnsweredItems(0)
                                    .lastAnsweredTime(Optional.empty())
                                    .build())
                            .build())
                    .build());
        }

        @Test
        void latest_answered_time_calculation() throws JsonProcessingException {
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

            scoreboardProcessor.computeToString(
                    mapper,
                    state,
                    contest,
                    styleModuleConfig,
                    contestantStartTimesMap,
                    ImmutableList.of(),
                    submissions,
                    Optional.empty());

            verify(mapper).writeValueAsString(new BundleScoreboard.Builder()
                    .state(state)
                    .content(new BundleScoreboardContent.Builder()
                            .addEntries(new BundleScoreboardEntry.Builder()
                                    .rank(1)
                                    .contestantJid("c1")
                                    .answeredItems(ImmutableList.of(1, 2))
                                    .totalAnsweredItems(3)
                                    .lastAnsweredTime(Instant.ofEpochSecond(600))
                                    .build())
                            .addEntries(new BundleScoreboardEntry.Builder()
                                    .rank(2)
                                    .contestantJid("c2")
                                    .answeredItems(ImmutableList.of(1, 0))
                                    .totalAnsweredItems(1)
                                    .lastAnsweredTime(Instant.ofEpochSecond(501))
                                    .build())
                            .build())
                    .build());
        }

        @Nested
        class Sorting {

            @Test
            void total_answered_items_over_last_answered_time() throws JsonProcessingException {
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

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        ImmutableList.of(),
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new BundleScoreboard.Builder()
                        .state(state)
                        .content(new BundleScoreboardContent.Builder()
                                .addEntries(new BundleScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .answeredItems(ImmutableList.of(1, 1))
                                        .totalAnsweredItems(2)
                                        .lastAnsweredTime(Instant.ofEpochSecond(300))
                                        .build())
                                .addEntries(new BundleScoreboardEntry.Builder()
                                        .rank(2)
                                        .contestantJid("c2")
                                        .answeredItems(ImmutableList.of(1, 0))
                                        .totalAnsweredItems(1)
                                        .lastAnsweredTime(Instant.ofEpochSecond(400))
                                        .build())
                                .build())
                        .build());
            }

            @Test
            void last_answered_time_as_tiebreaker() throws JsonProcessingException {
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

                scoreboardProcessor.computeToString(
                        mapper,
                        state,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        ImmutableList.of(),
                        submissions,
                        Optional.empty());

                verify(mapper).writeValueAsString(new BundleScoreboard.Builder()
                        .state(state)
                        .content(new BundleScoreboardContent.Builder()
                                .addEntries(new BundleScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c2")
                                        .answeredItems(ImmutableList.of(1, 0))
                                        .totalAnsweredItems(1)
                                        .lastAnsweredTime(Instant.ofEpochSecond(400))
                                        .build())
                                .addEntries(new BundleScoreboardEntry.Builder()
                                        .rank(1)
                                        .contestantJid("c1")
                                        .answeredItems(ImmutableList.of(1, 0))
                                        .totalAnsweredItems(1)
                                        .lastAnsweredTime(Instant.ofEpochSecond(300))
                                        .build())
                                .build())
                        .build());
            }
        }
    }

    @Test
    void filter_contestant_jids() {
        BundleScoreboardEntry entry = new BundleScoreboardEntry.Builder()
                .rank(1)
                .contestantJid("c2")
                .answeredItems(ImmutableList.of(1, 0))
                .totalAnsweredItems(1)
                .lastAnsweredTime(Instant.ofEpochSecond(400))
                .build();

        BundleScoreboard scoreboard = new BundleScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3", "c4")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new BundleScoreboardContent.Builder()
                        .addEntries(
                                new BundleScoreboardEntry.Builder().from(entry).contestantJid("c1").build(),
                                new BundleScoreboardEntry.Builder().from(entry).contestantJid("c2").build(),
                                new BundleScoreboardEntry.Builder().from(entry).contestantJid("c3").build(),
                                new BundleScoreboardEntry.Builder().from(entry).contestantJid("c4").build()
                        )
                        .build())
                .build();

        BundleScoreboard filteredScoreboard = new BundleScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c3")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new BundleScoreboardContent.Builder()
                        .addEntries(
                                new BundleScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c1").build(),
                                new BundleScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c3").build()
                        )
                        .build())
                .build();

        assertThat(scoreboardProcessor.filterContestantJids(scoreboard, ImmutableSet.of("c1", "c3")))
                .isEqualTo(filteredScoreboard);
    }

    @Test
    void pagination() {
        ScoreboardState state = new ScoreboardState.Builder()
                .addContestantJids("c1", "c2")
                .addProblemJids("p1", "p2")
                .addProblemAliases("A", "B")
                .build();

        List<BundleScoreboardEntry> fakeEntries = new ArrayList<>(134);
        for (int i = 0; i < 134; i++) {
            fakeEntries.add(mock(BundleScoreboardEntry.class));
        }

        BundleScoreboard bundleScoreboard = new BundleScoreboard.Builder()
                .state(state)
                .content(new BundleScoreboardContent.Builder()
                        .entries(fakeEntries)
                        .build())
                .build();

        BundleScoreboard pagedScoreboard = (BundleScoreboard) scoreboardProcessor.paginate(bundleScoreboard, 1, 50);
        System.out.println(pagedScoreboard);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(0, 50));

        pagedScoreboard = (BundleScoreboard) scoreboardProcessor.paginate(bundleScoreboard, 2, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(50, 100));

        pagedScoreboard = (BundleScoreboard) scoreboardProcessor.paginate(bundleScoreboard, 3, 50);
        assertThat(pagedScoreboard.getContent().getEntries()).isEqualTo(fakeEntries.subList(100, 134));
    }
}
