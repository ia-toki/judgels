package judgels.uriel.contest.scoreboard;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestScoreboardModel_;
import judgels.uriel.persistence.Daos.ContestScoreboardDao;

public class ContestScoreboardStore {
    private final ContestScoreboardDao contestScoreboardDao;

    @Inject
    public ContestScoreboardStore(ContestScoreboardDao contestScoreboardDao) {
        this.contestScoreboardDao = contestScoreboardDao;
    }

    public Optional<ContestScoreboardData> findScoreboard(String contestJid, ContestScoreboardType type) {
        return contestScoreboardDao.selectByUniqueColumns(ImmutableMap.of(
                ContestScoreboardModel_.contestJid, contestJid,
                ContestScoreboardModel_.type, type.name())).map(this::fromModel);
    }

    public Optional<ContestScoreboardData> upsertScoreboard(
            String contestJid,
            ContestScoreboardType type,
            String scoreboard) {

        Optional<ContestScoreboardModel> maybeModel = contestScoreboardDao.selectByUniqueColumns(ImmutableMap.of(
                ContestScoreboardModel_.contestJid, contestJid,
                ContestScoreboardModel_.type, type.name()));
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
