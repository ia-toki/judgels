package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.contest.ContestHacks.checkAllowed;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.contest.ContestStore;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ContestStore contestStore;
    private final ContestScoreboardStore contestScoreboardStore;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardResource(
            ContestStore contestStore,
            ContestScoreboardStore contestScoreboardStore,
            ObjectMapper mapper) {

        this.contestStore = contestStore;
        this.contestScoreboardStore = contestScoreboardStore;
        this.mapper = mapper;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getScoreboard(String contestJid) {
        // TODO(fushar): this should return frozen scoreboard when necessary

        return getScoreboardOfType(contestJid, ContestScoreboardType.OFFICIAL);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getFrozenScoreboard(String contestJid) {
        return getScoreboardOfType(contestJid, ContestScoreboardType.FROZEN);
    }

    private ContestScoreboard getScoreboardOfType(String contestJid, ContestScoreboardType type) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));
        ContestScoreboardData data =
                checkFound(contestScoreboardStore.findScoreboard(contestJid, type));

        Scoreboard scoreboard;
        try {
            scoreboard = mapper.readValue(data.getScoreboard(), IcpcScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ContestScoreboard.Builder()
                .type(type)
                .scoreboard(scoreboard)
                .build();
    }
}
