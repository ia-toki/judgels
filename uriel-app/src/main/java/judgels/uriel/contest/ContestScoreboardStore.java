package judgels.uriel.contest;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.ContestScoreboard;

public class ContestScoreboardStore {
    private final ContestDao contestDao;
    private final ContestScoreboardDao contestScoreboardDao;

    @Inject
    public ContestScoreboardStore(ContestDao contestDao, ContestScoreboardDao contestScoreboardDao) {
        this.contestDao = contestDao;
        this.contestScoreboardDao = contestScoreboardDao;
    }

    private static void toModel(String contestJid, boolean isOfficial, String scoreboard,
            ContestScoreboardModel model) {
        model.contestJid = contestJid;
        model.isOfficial = isOfficial;
        model.scoreboard = scoreboard;
    }

    public Optional<ContestScoreboard> findContestScoreboard(String contestJid, boolean isOfficial) {
        return contestScoreboardDao.selectByContestJid(contestJid, isOfficial).map(this::fromModel);
    }

    public Optional<ContestScoreboard> upsertContestScoreboard(String contestJid, boolean isOfficial,
            String scoreboard) {
        Optional<ContestModel> maybeContestModel = contestDao.selectByJid(contestJid);
        if (!maybeContestModel.isPresent()) {
            return Optional.empty();
        } else {
            Optional<ContestScoreboardModel> maybeContestScoreboardModel = contestScoreboardDao.selectByContestJid(
                    contestJid, isOfficial);
            if (!maybeContestScoreboardModel.isPresent()) {
                ContestScoreboardModel contestScoreboardModel = new ContestScoreboardModel();
                contestScoreboardModel.contestJid = contestJid;
                contestScoreboardModel.isOfficial = isOfficial;
                contestScoreboardModel.scoreboard = scoreboard;

                return Optional.of(fromModel(contestScoreboardDao.insert(contestScoreboardModel)));
            } else {
                ContestScoreboardModel contestScoreboardModel = maybeContestScoreboardModel.get();
                toModel(contestJid, isOfficial, scoreboard, contestScoreboardModel);

                return Optional.of(fromModel(contestScoreboardDao.update(contestScoreboardModel)));
            }
        }
    }

    private ContestScoreboard fromModel(ContestScoreboardModel model) {
        return new ContestScoreboard.Builder()
                .contestJid(model.contestJid)
                .isOfficial(model.isOfficial)
                .scoreboard(model.scoreboard)
                .build();
    }
}
