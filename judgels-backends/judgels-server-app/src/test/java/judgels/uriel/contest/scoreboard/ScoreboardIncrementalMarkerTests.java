package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.contest.scoreboard.ioi.IoiScoreboardIncrementalContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScoreboardIncrementalMarkerTests {
    private ScoreboardIncrementalMarker marker;

    @BeforeEach
    void before() {
        marker = new ScoreboardIncrementalMarker(Clock.systemUTC());
    }

    @Test
    void test() {
        ScoreboardIncrementalMarkKey key = new ScoreboardIncrementalMarkKey.Builder()
                .style(ContestStyle.ICPC)
                .beginTime(Instant.ofEpochSecond(2))
                .duration(Duration.ofHours(5))
                .styleModuleConfig(new IcpcStyleModuleConfig.Builder().build())
                .build();

        ScoreboardIncrementalMark mark = marker.getMark("contestJid", key);
        assertThat(mark.getKey()).isEmpty();
        assertThat(mark.getLastSubmissionId()).isEqualTo(0);
        assertThat(mark.getIncrementalContents()).isEmpty();

        IoiScoreboardIncrementalContent content = new IoiScoreboardIncrementalContent.Builder()
                .lastSubmissionId(3)
                .build();

        marker.setMark("contestJid", key, mark.getTimestamp(), ImmutableMap.of(OFFICIAL, content));
        mark = marker.getMark("contestJid", key);
        assertThat(mark.getKey()).contains(key);
        assertThat(mark.getLastSubmissionId()).isEqualTo(3);

        content = new IoiScoreboardIncrementalContent.Builder()
                .build();

        marker.setMark("contestJid", key, mark.getTimestamp(), ImmutableMap.of(OFFICIAL, content));
        mark = marker.getMark("contestJid", key);
        assertThat(mark.getKey()).contains(key);
        assertThat(mark.getLastSubmissionId()).isEqualTo(3);

        key = new ScoreboardIncrementalMarkKey.Builder()
                .from(key)
                .beginTime(Instant.ofEpochSecond(3))
                .build();
        mark = marker.getMark("contestJid", key);
        assertThat(mark.getKey()).isEmpty();
        assertThat(mark.getLastSubmissionId()).isEqualTo(0);
        assertThat(mark.getIncrementalContents()).isEmpty();
    }
}
