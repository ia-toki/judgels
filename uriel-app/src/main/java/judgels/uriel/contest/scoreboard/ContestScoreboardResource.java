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
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.role.RoleChecker;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestScoreboardStore contestScoreboardStore;
    private final UserService userService;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestScoreboardStore contestScoreboardStore,
            UserService userService,
            ObjectMapper mapper) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestScoreboardStore = contestScoreboardStore;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getScoreboard(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadContest(actorJid, contestJid));

        // TODO(fushar): this should return frozen scoreboard when necessary

        return getScoreboardOfType(authHeader, contestJid, ContestScoreboardType.OFFICIAL);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getFrozenScoreboard(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadContest(actorJid, contestJid));

        return getScoreboardOfType(authHeader, contestJid, ContestScoreboardType.FROZEN);
    }

    private ContestScoreboard getScoreboardOfType(AuthHeader authHeader, String contestJid,
            ContestScoreboardType type) {
        ContestScoreboardData data =
                checkFound(contestScoreboardStore.findScoreboard(contestJid, type));

        Scoreboard scoreboard;
        Set<String> contestantJids;
        try {
            IcpcScoreboard icpcScoreboard = mapper.readValue(data.getScoreboard(), IcpcScoreboard.class);
            scoreboard = icpcScoreboard;
            contestantJids = icpcScoreboard.getState().getContestantJids();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
