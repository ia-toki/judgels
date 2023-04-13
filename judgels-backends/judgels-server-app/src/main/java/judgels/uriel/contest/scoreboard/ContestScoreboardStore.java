package judgels.uriel.contest.scoreboard;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestScoreboardModel;

public class ContestScoreboardStore {
    private final ContestScoreboardDao scoreboardDao;

    private final Cache<String, RawContestScoreboard> scoreboardCache;

    @Inject
    public ContestScoreboardStore(ContestScoreboardDao scoreboardDao) {
        this.scoreboardDao = scoreboardDao;

        this.scoreboardCache = Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(getShortDuration())
                .build();
    }

    public Optional<RawContestScoreboard> getScoreboard(String contestJid, ContestScoreboardType type) {
        return Optional.ofNullable(scoreboardCache.get(
                contestJid + SEPARATOR + type.name(),
                $ -> getScoreboardUncached(contestJid, type).orElse(null)));
    }

    private Optional<RawContestScoreboard> getScoreboardUncached(String contestJid, ContestScoreboardType type) {
        return scoreboardDao.selectByContestJidAndType(contestJid, type).map(this::fromModel);
    }

    public RawContestScoreboard upsertScoreboard(String contestJid, ContestScoreboardData data) {
        Optional<ContestScoreboardModel> maybeModel =
                scoreboardDao.selectByContestJidAndType(contestJid, data.getType());

        RawContestScoreboard scoreboard;
        if (maybeModel.isPresent()) {
            ContestScoreboardModel model = maybeModel.get();
            toModel(contestJid, data, model);
            scoreboard = fromModel(scoreboardDao.update(model));
        } else {
            ContestScoreboardModel model = new ContestScoreboardModel();
            toModel(contestJid, data, model);
            scoreboard = fromModel(scoreboardDao.insert(model));
        }

        scoreboardCache.invalidate(contestJid + SEPARATOR + data.getType().name());
        return scoreboard;
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
