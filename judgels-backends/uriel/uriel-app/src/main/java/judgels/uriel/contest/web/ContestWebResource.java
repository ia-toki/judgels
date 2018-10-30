package judgels.uriel.contest.web;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.api.contest.web.ContestWebService;
import judgels.uriel.api.contest.web.ContestWithWebConfig;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;

public class ContestWebResource implements ContestWebService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestWebConfigFetcher webConfigFetcher;

    @Inject
    public ContestWebResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestWebConfigFetcher webConfigFetcher) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.webConfigFetcher = webConfigFetcher;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestWithWebConfig getContestBySlugWithWebConfig(Optional<AuthHeader> authHeader, String contestSlug) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return new ContestWithWebConfig.Builder()
                .contest(contest)
                .config(webConfigFetcher.fetchConfig(actorJid, contest))
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestWithWebConfig getContestByJidWithWebConfig(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return new ContestWithWebConfig.Builder()
                .contest(contest)
                .config(webConfigFetcher.fetchConfig(actorJid, contest))
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestWebConfig getWebConfig(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return webConfigFetcher.fetchConfig(actorJid, contest);
    }
}
