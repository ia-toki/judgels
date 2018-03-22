package judgels.uriel.contest.scoreboard;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardModel;

public class ContestScoreboardStore {
    private final ContestScoreboardDao contestScoreboardDao;

    @Inject
    public ContestScoreboardStore(ContestScoreboardDao contestScoreboardDao) {
        this.contestScoreboardDao = contestScoreboardDao;
    }

    public Optional<ContestScoreboardData> findScoreboard(String contestJid, ContestScoreboardType type) {
        return contestScoreboardDao.selectByContestJidAndType(contestJid, type).map(this::fromModel);
    }

    public Optional<ContestScoreboardData> upsertScoreboard(
            String contestJid,
            ContestScoreboardType type,
            String scoreboard) {

        Optional<ContestScoreboardModel> maybeModel = contestScoreboardDao.selectByContestJidAndType(contestJid, type);
        if (!maybeModel.isPresent()) {
            ContestScoreboardModel model = new ContestScoreboardModel();
            toModel(contestJid, type, scoreboard, model);
            return Optional.of(fromModel(contestScoreboardDao.insert(model)));
        } else {
            ContestScoreboardModel model = maybeModel.get();
            toModel(contestJid, type, scoreboard, model);
            return Optional.of(fromModel(contestScoreboardDao.update(model)));
        }
    }

    private ContestScoreboardData fromModel(ContestScoreboardModel model) {
        return new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.valueOf(model.type))
                .scoreboard(model.scoreboard)
                .build();
    }

    private static void toModel(
            String contestJid,
            ContestScoreboardType type,
            String scoreboard,
            ContestScoreboardModel model) {

        model.contestJid = contestJid;
        model.type = type.name();
        model.scoreboard = scoreboard;
    }
}
