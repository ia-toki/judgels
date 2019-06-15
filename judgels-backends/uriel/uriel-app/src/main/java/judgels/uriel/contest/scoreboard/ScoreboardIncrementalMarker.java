package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

@Singleton
public class ScoreboardIncrementalMarker {
    private final Clock clock;
    private final Cache<String, ScoreboardIncrementalMark> marks;

    @Inject
    public ScoreboardIncrementalMarker(Clock clock) {
        this.clock = clock;
        this.marks = Caffeine.newBuilder()
                .maximumSize(100)
                .build();
    }

    public synchronized ScoreboardIncrementalMark getMark(
            String contestJid,
            ScoreboardIncrementalMarkKey key) {

        ScoreboardIncrementalMark mark = marks.get(contestJid, $ -> newEmptyMark());

        if (mark.getKey().isPresent() && !mark.getKey().get().equals(key)) {
            invalidateMark(contestJid);
            return getMark(contestJid, key);
        }
        return mark;
    }

    public synchronized void setMark(
            String contestJid,
            ScoreboardIncrementalMarkKey key,
            Instant lastTimestamp,
            Map<ContestScoreboardType, ScoreboardIncrementalContent> incrementalContents) {

        ScoreboardIncrementalMark mark = marks.get(contestJid, $ -> newEmptyMark());
        if (!lastTimestamp.equals(mark.getTimestamp())) {
            return;
        }

        Optional<Long> lastSubmissionId = Optional.ofNullable(incrementalContents.get(OFFICIAL))
                .flatMap(ScoreboardIncrementalContent::getLastSubmissionId);

        marks.put(contestJid, new ScoreboardIncrementalMark.Builder()
                .key(key)
                .timestamp(clock.instant())
                .lastSubmissionId(lastSubmissionId.orElse(mark.getLastSubmissionId()))
                .incrementalContents(incrementalContents)
                .build());
    }

    public synchronized void invalidateMark(String contestJid) {
        marks.put(contestJid, newEmptyMark());
    }

    private ScoreboardIncrementalMark newEmptyMark() {
        return new ScoreboardIncrementalMark.Builder()
                .timestamp(clock.instant())
                .lastSubmissionId(0L)
                .build();
    }
}
