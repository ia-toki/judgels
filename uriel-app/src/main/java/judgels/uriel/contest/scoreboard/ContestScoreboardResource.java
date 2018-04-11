package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.role.RoleChecker;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestScoreboardStore contestScoreboardStore;
    private final UserService userService;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestScoreboardStore contestScoreboardStore,
            UserService userService,
            ObjectMapper mapper) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.contestScoreboardStore = contestScoreboardStore;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getScoreboard(AuthHeader authHeader, String contestJid) {
        // TODO(fushar): this should return frozen scoreboard when necessary
        return getScoreboardOfType(authHeader, contestJid, ContestScoreboardType.OFFICIAL);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getFrozenScoreboard(AuthHeader authHeader, String contestJid) {
        return getScoreboardOfType(authHeader, contestJid, ContestScoreboardType.FROZEN);
    }

    private ContestScoreboard getScoreboardOfType(
            AuthHeader authHeader,
            String contestJid,
            ContestScoreboardType type) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadContest(actorJid, contestJid));

        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        ContestScoreboardData data = checkFound(contestScoreboardStore.findScoreboard(contestJid, type));

        Scoreboard scoreboard;
        try {
            if (contest.getStyle() == ContestStyle.ICPC) {
                scoreboard = mapper.readValue(data.getScoreboard(), IcpcScoreboard.class);
            } else {
                scoreboard = mapper.readValue(data.getScoreboard(), IoiScoreboard.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<String> contestantJids = scoreboard.getState().getContestantJids();

        Map<String, User> usersByJids = userService.findUsersByJids(contestantJids);
        Map<String, String> contestantDisplayNames = usersByJids.values().stream()
                .collect(Collectors.toMap(User::getJid, User::getUsername));

        return new ContestScoreboard.Builder()
                .type(type)
                .scoreboard(scoreboard)
                .contestantDisplayNames(contestantDisplayNames)
                .build();
    }
}
