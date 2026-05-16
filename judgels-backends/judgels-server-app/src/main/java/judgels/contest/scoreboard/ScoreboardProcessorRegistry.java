package judgels.contest.scoreboard;

import jakarta.inject.Inject;
import judgels.api.contest.ContestStyle;
import judgels.contest.scoreboard.bundle.BundleScoreboardProcessor;
import judgels.contest.scoreboard.gcj.GcjScoreboardProcessor;
import judgels.contest.scoreboard.icpc.IcpcScoreboardProcessor;
import judgels.contest.scoreboard.ioi.IoiScoreboardProcessor;
import judgels.contest.scoreboard.troc.TrocScoreboardProcessor;

public class ScoreboardProcessorRegistry {
    @Inject
    public ScoreboardProcessorRegistry() {}

    public ScoreboardProcessor get(ContestStyle style) {
        if (style == ContestStyle.TROC) {
            return new TrocScoreboardProcessor();
        } else if (style == ContestStyle.ICPC) {
            return new IcpcScoreboardProcessor();
        } else if (style == ContestStyle.IOI) {
            return new IoiScoreboardProcessor();
        } else if (style == ContestStyle.GCJ) {
            return new GcjScoreboardProcessor();
        } else if (style == ContestStyle.BUNDLE) {
            return new BundleScoreboardProcessor();
        }
        throw new IllegalArgumentException();
    }
}
