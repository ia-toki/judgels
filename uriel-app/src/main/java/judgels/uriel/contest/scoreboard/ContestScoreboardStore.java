package judgels.uriel.contest.scoreboard;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestScoreboardModel;

public class ContestScoreboardStore {
    private final ContestScoreboardDao contestScoreboardDao;

    @Inject
    public ContestScoreboardStore(ContestScoreboardDao contestScoreboardDao) {
        this.contestScoreboardDao = contestScoreboardDao;
    }

    public Optional<RawContestScoreboard> findScoreboard(String contestJid, ContestScoreboardType type) {
        return contestScoreboardDao.selectByContestJidAndType(contestJid, type).map(this::fromModel);
    }

    public RawContestScoreboard upsertScoreboard(String contestJid, ContestScoreboardData data) {
        Optional<ContestScoreboardModel> maybeModel =
                contestScoreboardDao.selectByContestJidAndType(contestJid, data.getType());

        if (maybeModel.isPresent()) {
            ContestScoreboardModel model = maybeModel.get();
            toModel(contestJid, data, model);
            return fromModel(contestScoreboardDao.update(model));
        } else {
            ContestScoreboardModel model = new ContestScoreboardModel();
            toModel(contestJid, data, model);
            return fromModel(contestScoreboardDao.insert(model));
        }
    }

    private RawContestScoreboard fromModel(ContestScoreboardModel model) {
        return new RawContestScoreboard.Builder()
                .type(ContestScoreboardType.valueOf(model.type))
                .scoreboard(model.scoreboard)
                .updatedTime(model.updatedAt)
                .build();
    }

    private static void toModel(String contestJid, ContestScoreboardData data, ContestScoreboardModel model) {
        model.contestJid = contestJid;
        model.type = data.getType().name();
        model.scoreboard = data.getScoreboard();
    }
}
