package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;

public class ContestScoreboardFetcher {
    private final ContestScoreboardStore scoreboardStore;
    private final UserService userService;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardFetcher(
            ContestScoreboardStore scoreboardStore,
            UserService userService,
            ObjectMapper mapper) {

        this.scoreboardStore = scoreboardStore;
        this.userService = userService;
        this.mapper = mapper;
    }

    public ContestScoreboardResponse fetchScoreboard(
            String contestJid,
            ContestStyle contestStyle,
            ContestScoreboardType type) {

        ContestScoreboardData data = checkFound(scoreboardStore.findScoreboard(contestJid, type));

        Scoreboard scoreboard;
        try {
            if (contestStyle == ContestStyle.ICPC) {
                scoreboard = mapper.readValue(data.getScoreboard(), IcpcScoreboard.class);
            } else {
                scoreboard = mapper.readValue(data.getScoreboard(), IoiScoreboard.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<String> userJids = scoreboard.getState().getContestantJids();
        Map<String, UserInfo> usersMap = userService.findUsersByJids(userJids);

        return new ContestScoreboardResponse.Builder()
                .data(new ContestScoreboard.Builder()
                        .type(type)
                        .scoreboard(scoreboard)
                        .build())
                .usersMap(usersMap)
                .build();
    }
}
