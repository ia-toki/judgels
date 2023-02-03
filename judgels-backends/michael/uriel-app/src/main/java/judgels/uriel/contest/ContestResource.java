package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.ActiveContestsResponse;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestConfig;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.ContestsResponse;
import judgels.uriel.api.contest.dump.ContestsDump;
import judgels.uriel.api.contest.dump.ExportContestsDumpData;
import judgels.uriel.api.contest.dump.ImportContestsDumpResponse;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestResource implements ContestService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestModuleStore moduleStore;
    private final ContestContestantStore contestantStore;
    private final UserClient userClient;

    @Inject
    public ContestResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContest(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork
    public Contest updateContest(AuthHeader authHeader, String contestJid, ContestUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        contest = checkFound(contestStore.updateContest(contestJid, data));

        contestLogger.log(contestJid, "UPDATE_CONTEST");

        return contest;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContestBySlug(Optional<AuthHeader> authHeader, String contestSlug) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork
    public void startVirtualContest(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canStartVirtual(actorJid, contest));

        contestantStore.startVirtualContest(contestJid, actorJid);

        contestLogger.log(contestJid, "START_VIRTUAL_CONTEST");
    }

    @Override
    @UnitOfWork
    public void resetVirtualContest(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canResetVirtual(actorJid, contest));

        contestantStore.resetVirtualContest(contestJid);

        contestLogger.log(contestJid, "RESET_VIRTUAL_CONTEST");
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestsResponse getContests(
            Optional<AuthHeader> authHeader,
            Optional<String> name,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        Page<Contest> contests = contestStore.getContests(actorJid, isAdmin, name, page);
        Map<String, ContestRole> rolesMap = contests.getPage()
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));
        boolean canAdminister = contestRoleChecker.canAdminister(actorJid);
        ContestConfig config = new ContestConfig.Builder()
                .canAdminister(canAdminister)
                .build();

        return new ContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .config(config)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ActiveContestsResponse getActiveContests(Optional<AuthHeader> authHeader) {
        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        List<Contest> contests = contestStore.getActiveContests(actorJid, isAdmin);
        Map<String, ContestRole> rolesMap = contests
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));

        return new ActiveContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public Contest createContest(AuthHeader authHeader, ContestCreateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        Contest contest = contestStore.createContest(data);
        moduleStore.upsertIcpcStyleModule(contest.getJid(), new IcpcStyleModuleConfig.Builder().build());

        contestLogger.log(contest.getJid(), "CREATE_CONTEST");

        return contest;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestDescription getContestDescription(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));

        contestLogger.log(contest.getJid(), "OPEN_CONTEST");

        String description = checkFound(contestStore.getContestDescription(contest.getJid()));
        return new ContestDescription.Builder()
                .description(description)
                .profilesMap(userClient.parseProfiles(description))
                .build();
    }

    @Override
    @UnitOfWork
    public ContestDescription updateContestDescription(
            AuthHeader authHeader,
            String contestJid,
            ContestDescription description) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        ContestDescription newDescription =
                checkFound(contestStore.updateContestDescription(contest.getJid(), description));

        contestLogger.log(contest.getJid(), "UPDATE_DESCRIPTION");

        return newDescription;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestsDump exportDump(AuthHeader authHeader, ExportContestsDumpData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        return new ContestsDump.Builder()
                .contests(contestStore.exportDumps(data.getContests()))
                .build();
    }

    @Override
    @UnitOfWork
    public ImportContestsDumpResponse importDump(AuthHeader authHeader, ContestsDump contestsDump) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        Set<String> createdContestJids = contestsDump.getContests().stream()
                .map(contestStore::importDump)
                .collect(Collectors.toSet());

        return new ImportContestsDumpResponse.Builder()
                .addAllCreatedContestJids(createdContestJids)
                .build();
    }
}
