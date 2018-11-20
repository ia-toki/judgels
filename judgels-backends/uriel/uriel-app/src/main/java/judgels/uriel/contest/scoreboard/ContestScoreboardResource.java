package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.contest.ContestStore;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestScoreboardRoleChecker scoreboardRoleChecker;
    private final ContestScoreboardFetcher scoreboardFetcher;
    private final ProfileService profileService;

    @Inject
    public ContestScoreboardResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestScoreboardRoleChecker scoreboardRoleChecker,
            ContestScoreboardFetcher scoreboardFetcher,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.scoreboardRoleChecker = scoreboardRoleChecker;
        this.scoreboardFetcher = scoreboardFetcher;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getScoreboard(
            Optional<AuthHeader> authHeader,
            String contestJid,
            boolean frozen,
            boolean showClosedProblems) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canViewDefault(actorJid, contest));

        boolean canSupervise = scoreboardRoleChecker.canSupervise(actorJid, contest);
        boolean canViewOfficialAndFrozen = scoreboardRoleChecker.canViewOfficialAndFrozen(actorJid, contest);
        boolean canViewClosedProblems = scoreboardRoleChecker.canViewClosedProblems(actorJid, contest);
        ContestScoreboardConfig config = new ContestScoreboardConfig.Builder()
                .canViewOfficialAndFrozen(canViewOfficialAndFrozen)
                .canViewClosedProblems(canViewClosedProblems)
                .build();

        if (showClosedProblems) {
            checkAllowed(canSupervise);
        }

        return scoreboardFetcher.fetchScoreboard(contest, actorJid, canSupervise, frozen, showClosedProblems)
                .map(scoreboard -> {
                    Set<String> contestantJids = scoreboard.getScoreboard().getState().getContestantJids();
                    Map<String, Profile> profilesMap = contestantJids.isEmpty()
                            ? Collections.emptyMap()
                            : profileService.getProfiles(contestantJids, scoreboard.getUpdatedTime());

                    return new ContestScoreboardResponse.Builder()
                            .data(scoreboard)
                            .profilesMap(profilesMap)
                            .config(config)
                            .build();
                });
    }
}
