package judgels.uriel.contest.group;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.scoreboard.ContestScoreboardData;
import judgels.uriel.contest.scoreboard.RawContestScoreboard;
import judgels.uriel.persistence.ContestGroupScoreboardDao;
import judgels.uriel.persistence.ContestGroupScoreboardModel;

public class ContestGroupScoreboardStore {
    private final ContestGroupScoreboardDao scoreboardDao;

    @Inject
    public ContestGroupScoreboardStore(ContestGroupScoreboardDao scoreboardDao) {
        this.scoreboardDao = scoreboardDao;
    }

    public RawContestScoreboard upsertScoreboard(String contestGroupJid, ContestScoreboardData data) {
        Optional<ContestGroupScoreboardModel> maybeModel =
                scoreboardDao.selectByContestGroupJidAndType(contestGroupJid, data.getType());

        if (maybeModel.isPresent()) {
            ContestGroupScoreboardModel model = maybeModel.get();
            toModel(contestGroupJid, data, model);
            return fromModel(scoreboardDao.update(model));
        } else {
            ContestGroupScoreboardModel model = new ContestGroupScoreboardModel();
            toModel(contestGroupJid, data, model);
            return fromModel(scoreboardDao.insert(model));
        }
    }

    private RawContestScoreboard fromModel(ContestGroupScoreboardModel model) {
        return new RawContestScoreboard.Builder()
                .type(ContestScoreboardType.valueOf(model.type))
                .scoreboard(model.scoreboard)
                .updatedTime(model.updatedAt)
                .build();
    }

    private static void toModel(String contestGroupJid, ContestScoreboardData data, ContestGroupScoreboardModel model) {
        model.contestGroupJid = contestGroupJid;
        model.type = data.getType().name();
        model.scoreboard = data.getScoreboard();
    }
}
