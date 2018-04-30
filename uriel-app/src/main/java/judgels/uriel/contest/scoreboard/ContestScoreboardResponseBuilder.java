package judgels.uriel.contest.scoreboard;

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

public class ContestScoreboardResponseBuilder {
    private final UserService userService;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardResponseBuilder(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    public ContestScoreboardResponse buildResponse(
            RawContestScoreboard raw,
            ContestStyle contestStyle,
            ContestScoreboardType type) {

        Scoreboard scoreboard;
        try {
            if (contestStyle == ContestStyle.ICPC) {
                scoreboard = mapper.readValue(raw.getScoreboard(), IcpcScoreboard.class);
            } else {
                scoreboard = mapper.readValue(raw.getScoreboard(), IoiScoreboard.class);
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
                        .updatedTime(raw.getUpdatedTime())
                        .build())
                .usersMap(usersMap)
                .build();
    }
}
