package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.module.ContestModuleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestScoreboardTypeFetcherTests {
    private static final String CONTEST_JID = "contestJid";
    private static final Duration FREEZE_DURATION = Duration.ofSeconds(10);

    @Mock private ContestModuleStore moduleStore;
    @Mock private Clock clock;
    @Mock private Contest contest;

    private ContestScoreboardTypeFetcher typeFetcher;

    @BeforeEach
    void before() {
        initMocks(this);

        when(contest.getJid()).thenReturn(CONTEST_JID);
        when(contest.getEndTime()).thenReturn(Instant.ofEpochSecond(52));

        typeFetcher = new ContestScoreboardTypeFetcher(moduleStore, clock);
    }

    @Test
    void when_no_frozen_scoreboard_module() {
        assertThatContestantsSee(OFFICIAL);
        assertThatSupervisorsSee(OFFICIAL);
    }

    @Nested
    class when_has_frozen_scoreboard_module {
        @Nested
        class when_not_unfrozen_yet {
            @BeforeEach
            void before() {
                when(moduleStore.getFrozenScoreboardModuleConfig(CONTEST_JID))
                        .thenReturn(Optional.of(new FrozenScoreboardModuleConfig.Builder()
                                .freezeDurationBeforeEndTime(FREEZE_DURATION)
                                .isOfficialScoreboardAllowed(false)
                                .build()));
            }

            @Test
            void when_before_freeze_time() {
                when(clock.instant()).thenReturn(Instant.ofEpochSecond(41));
                assertThatContestantsSee(OFFICIAL);
                assertThatSupervisorsSee(OFFICIAL);
            }

            @Test
            void when_at_freeze_time() {
                when(clock.instant()).thenReturn(Instant.ofEpochSecond(42));
                assertThatContestantsSee(FROZEN);
                assertThatSupervisorsSee(OFFICIAL, FROZEN);
            }

            @Test
            void when_after_freeze_time() {
                when(clock.instant()).thenReturn(Instant.ofEpochSecond(43));
                assertThatContestantsSee(FROZEN);
                assertThatSupervisorsSee(OFFICIAL, FROZEN);
            }
        }

        @Nested
        class when_unfrozen {
            @BeforeEach
            void before() {
                when(moduleStore.getFrozenScoreboardModuleConfig(CONTEST_JID))
                        .thenReturn(Optional.of(new FrozenScoreboardModuleConfig.Builder()
                                .freezeDurationBeforeEndTime(FREEZE_DURATION)
                                .isOfficialScoreboardAllowed(true)
                                .build()));
            }

            @Test
            void when_before_freeze_time() {
                when(clock.instant()).thenReturn(Instant.ofEpochSecond(41));
                assertThatContestantsSee(OFFICIAL, FROZEN);
                assertThatSupervisorsSee(OFFICIAL, FROZEN);
            }

            @Test
            void when_at_freeze_time() {
                when(clock.instant()).thenReturn(Instant.ofEpochSecond(42));
                assertThatContestantsSee(OFFICIAL, FROZEN);
                assertThatSupervisorsSee(OFFICIAL, FROZEN);
            }

            @Test
            void when_after_freeze_time() {
                when(clock.instant()).thenReturn(Instant.ofEpochSecond(43));
                assertThatContestantsSee(OFFICIAL, FROZEN);
                assertThatSupervisorsSee(OFFICIAL, FROZEN);
            }
        }
    }

    private void assertThatSupervisorsSee(ContestScoreboardType... types) {
        assertThat(typeFetcher.fetchViewableTypes(contest, true)).containsExactly(types);
    }

    private void assertThatContestantsSee(ContestScoreboardType... types) {
        assertThat(typeFetcher.fetchViewableTypes(contest, false)).containsExactly(types);
    }
}
