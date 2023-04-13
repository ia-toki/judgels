package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.persistence.TestClock;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ContestTimerTests {
    private static final String CONTEST = "contestJid";
    private static final String USER = "userJid";
    private static final String GUEST = "guest";

    @Mock private ContestContestantStore contestantStore;
    @Mock private ContestModuleStore moduleStore;

    private Contest contest;

    @BeforeEach
    void before() {
        initMocks(this);
    }

    /**
     * 12345678
     * --|--|--
     *   B  E
     */
    @Nested
    class non_virtual_or_not_started {
        @BeforeEach
        void before() {
            contest = mockContest(3, 6);
        }

        @Test
        void test_states() {
            assertStates(1, false, false);
            assertStates(2, false, false);
            assertStates(3, true, false);
            assertStates(4, true, false);
            assertStates(5, true, false);
            assertStates(6, true, true);
            assertStates(7, true, true);
            assertStates(8, true, true);
        }

        @Test
        void test_durations() {
            assertDurations(1, 2, -1, 5);
            assertDurations(2, 1, -1, 4);
            assertDurations(3, 0, 0, 3);
            assertDurations(4, -1, 1, 2);
            assertDurations(5, -1, 2, 1);
            assertDurations(6, -1, 3, 0);
        }

        private void assertStates(long at, boolean begun, boolean ended) {
            Clock clock = new TestClock(Instant.ofEpochSecond(at));

            when(moduleStore.getVirtualModuleConfig(CONTEST)).thenReturn(Optional.empty());
            ContestTimer timer = new ContestTimer(contestantStore, moduleStore, clock);
            doAssertStates(timer, contest, GUEST, begun, begun, ended, ended);

            when(moduleStore.getVirtualModuleConfig(CONTEST))
                    .thenReturn(Optional.of(new VirtualModuleConfig.Builder()
                            .virtualDuration(Duration.ofSeconds(1))
                            .build()));
            when(contestantStore.getContestant(CONTEST, USER)).thenReturn(
                    Optional.of(new ContestContestant.Builder()
                            .userJid(USER)
                            .build()));
            timer = new ContestTimer(contestantStore, moduleStore, clock);
            doAssertStates(timer, contest, USER, begun, false, ended, ended);
        }

        private void assertDurations(long at, long toBegin, long fromBegin, long toEnd) {
            Clock clock = new TestClock(Instant.ofEpochSecond(at));

            when(moduleStore.getVirtualModuleConfig(CONTEST)).thenReturn(Optional.empty());
            ContestTimer timer = new ContestTimer(contestantStore, moduleStore, clock);
            doAssertDurations(timer, contest, GUEST, toBegin, fromBegin, toEnd, toEnd);

            when(moduleStore.getVirtualModuleConfig(CONTEST))
                    .thenReturn(Optional.of(new VirtualModuleConfig.Builder()
                            .virtualDuration(Duration.ofSeconds(1))
                            .build()));
            when(contestantStore.getContestant(CONTEST, USER)).thenReturn(
                    Optional.of(new ContestContestant.Builder()
                            .userJid(USER)
                            .build()));
            timer = new ContestTimer(contestantStore, moduleStore, clock);
            doAssertDurations(timer, contest, USER, toBegin, fromBegin, toEnd, toEnd);
        }
    }

    @Nested
    class virtual {
        /**
         * 12345678901234
         * --|--|--|--|--
         *   B  S  F  E
         */
        @Nested
        class finish_before_end {
            @BeforeEach
            void before() {
                contest = mockContest(3, 12);
                when(moduleStore.getVirtualModuleConfig(CONTEST))
                        .thenReturn(Optional.of(new VirtualModuleConfig.Builder()
                                .virtualDuration(Duration.ofSeconds(3))
                                .build()));
            }

            @Test
            void test_states() {
                assertStates(1, false, false, false, false);
                assertStates(2, false, false, false, false);
                assertStates(3, true, false, false, false);
                assertStates(4, true, false, false, false);
                assertStates(5, true, false, false, false);
                assertStates(6, true, true, false, false);
                assertStates(7, true, true, false, false);
                assertStates(8, true, true, false, false);
                assertStates(9, true, true, true, false);
                assertStates(10, true, true, true, false);
                assertStates(11, true, true, true, false);
                assertStates(12, true, true, true, true);
                assertStates(13, true, true, true, true);
                assertStates(14, true, true, true, true);
            }

            @Test
            void test_durations() {
                assertDurations(1, 2, -1, -1, 11);
                assertDurations(2, 1, -1, -1, 10);
                assertDurations(3, 0, 0, -1, 9);
                assertDurations(4, -1, 1, -1, 8);
                assertDurations(5, -1, 2, -1, 7);
                assertDurations(6, -1, 3, 3, 6);
                assertDurations(7, -1, 4, 2, 5);
                assertDurations(8, -1, 5, 1, 4);
                assertDurations(9, -1, 6, 0, 3);
                assertDurations(10, -1, -1, -1, 2);
                assertDurations(11, -1, -1, -1, 1);
                assertDurations(12, -1, -1, -1, 0);
            }
        }

        /**
         * 12345678901234
         * --|--|--|--|--
         *   B  S  E  F
         */
        @Nested
        class finish_after_end {
            @BeforeEach
            void before() {
                contest = mockContest(3, 9);
                when(moduleStore.getVirtualModuleConfig(CONTEST))
                        .thenReturn(Optional.of(new VirtualModuleConfig.Builder()
                                .virtualDuration(Duration.ofSeconds(6))
                                .build()));
            }

            @Test
            void test() {
                assertStates(1, false, false, false, false);
                assertStates(2, false, false, false, false);
                assertStates(3, true, false, false, false);
                assertStates(4, true, false, false, false);
                assertStates(5, true, false, false, false);
                assertStates(6, true, true, false, false);
                assertStates(7, true, true, false, false);
                assertStates(8, true, true, false, false);
                assertStates(9, true, true, true, true);
                assertStates(10, true, true, true, true);
                assertStates(11, true, true, true, true);
                assertStates(12, true, true, true, true);
                assertStates(13, true, true, true, true);
                assertStates(14, true, true, true, true);
            }

            @Test
            void test_durations() {
                assertDurations(1, 2, -1, -1, 8);
                assertDurations(2, 1, -1, -1, 7);
                assertDurations(3, 0, 0, -1, 6);
                assertDurations(4, -1, 1, -1, 5);
                assertDurations(5, -1, 2, -1, 4);
                assertDurations(6, -1, 3, 3, 3);
                assertDurations(7, -1, 4, 2, 2);
                assertDurations(8, -1, 5, 1, 1);
                assertDurations(9, -1, 6, 0, 0);
            }
        }

        private void assertStates(long at, boolean begun, boolean started, boolean finished, boolean ended) {
            Clock clock = new TestClock(Instant.ofEpochSecond(at));

            when(contestantStore.getContestant(CONTEST, USER)).thenReturn(
                    Optional.of(new ContestContestant.Builder()
                            .userJid(USER)
                            .contestStartTime(Instant.ofEpochSecond(6))
                            .build()));
            ContestTimer timer = new ContestTimer(contestantStore, moduleStore, clock);
            doAssertStates(timer, contest, USER, begun, started, finished, ended);
        }

        private void assertDurations(long at, long toBegin, long fromBegin, long toFinish, long toEnd) {
            Clock clock = new TestClock(Instant.ofEpochSecond(at));

            when(contestantStore.getContestant(CONTEST, USER)).thenReturn(
                    Optional.of(new ContestContestant.Builder()
                            .userJid(USER)
                            .contestStartTime(Instant.ofEpochSecond(6))
                            .build()));
            ContestTimer timer = new ContestTimer(contestantStore, moduleStore, clock);
            doAssertDurations(timer, contest, USER, toBegin, fromBegin, toFinish, toEnd);
        }
    }

    private static Contest mockContest(long beginTime, long endTime) {
        Contest contest = mock(Contest.class);
        when(contest.getJid()).thenReturn(CONTEST);
        when(contest.getBeginTime()).thenReturn(Instant.ofEpochSecond(beginTime));
        when(contest.getDuration()).thenReturn(Duration.ofSeconds(endTime - beginTime));
        when(contest.getEndTime()).thenReturn(Instant.ofEpochSecond(endTime));
        return contest;
    }

    private static void doAssertStates(
            ContestTimer timer,
            Contest contest,
            String userJid,
            boolean begun,
            boolean started,
            boolean finished,
            boolean ended) {

        assertThat(timer.hasBegun(contest)).isEqualTo(begun);
        assertThat(timer.hasStarted(contest, userJid)).isEqualTo(started);
        assertThat(timer.hasFinished(contest, userJid)).isEqualTo(finished);
        assertThat(timer.hasEnded(contest)).isEqualTo(ended);
    }

    private static void doAssertDurations(
            ContestTimer timer,
            Contest contest,
            String userJid,
            long toBegin,
            long fromBegin,
            long toFinish,
            long toEnd) {

        if (toBegin != -1) {
            assertThat(timer.getDurationToBeginTime(contest)).isEqualTo(Duration.ofSeconds(toBegin));
        }
        if (fromBegin != -1) {
            assertThat(timer.getDurationFromBeginTime(contest)).isEqualTo(Duration.ofSeconds(fromBegin));
        }
        if (toFinish != -1) {
            assertThat(timer.getDurationToFinishTime(contest, userJid)).isEqualTo(Duration.ofSeconds(toFinish));
        }
        if (toEnd != -1) {
            assertThat(timer.getDurationToEndTime(contest)).isEqualTo(Duration.ofSeconds(toEnd));
        }
    }
}
