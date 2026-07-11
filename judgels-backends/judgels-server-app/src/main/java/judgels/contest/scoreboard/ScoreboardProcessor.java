package judgels.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import judgels.api.contest.module.StyleModuleConfig;
import judgels.api.contest.scoreboard.Scoreboard;
import judgels.api.contest.scoreboard.ScoreboardEntry;
import judgels.api.contest.scoreboard.ScoreboardState;

public interface ScoreboardProcessor {
    Scoreboard parse(ObjectMapper mapper, String json);
    Scoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries);

    boolean requiresGradingDetails(StyleModuleConfig styleModuleConfig);

    ScoreboardProcessResult process(ScoreboardProcessParams param);

    ScoreboardEntry clearEntryRank(ScoreboardEntry entry);
}
