package judgels.uriel.contest.scoreboard.icpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class IcpcScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public Scoreboard parseFromString(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, IcpcScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids) {
        IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard;

        Set<String> filteredContestantJids = icpcScoreboard.getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(icpcScoreboard.getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<IcpcScoreboardEntry> filteredEntries = icpcScoreboard.getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new IcpcScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new IcpcScoreboard.Builder()
                .state(filteredState)
                .content(new IcpcScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }
}
