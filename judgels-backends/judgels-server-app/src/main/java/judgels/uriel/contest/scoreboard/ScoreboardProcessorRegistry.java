package judgels.uriel.contest.scoreboard;

import javax.inject.Inject;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.contest.scoreboard.bundle.BundleScoreboardProcessor;
import judgels.uriel.contest.scoreboard.gcj.GcjScoreboardProcessor;
import judgels.uriel.contest.scoreboard.icpc.IcpcScoreboardProcessor;
import judgels.uriel.contest.scoreboard.ioi.IoiScoreboardProcessor;
import judgels.uriel.contest.scoreboard.troc.TrocScoreboardProcessor;

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
