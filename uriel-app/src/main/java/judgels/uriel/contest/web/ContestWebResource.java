package judgels.uriel.contest.web;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.api.contest.web.ContestWebService;
import judgels.uriel.contest.ContestStore;

public class ContestWebResource implements ContestWebService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestWebConfigFetcher webConfigFetcher;

    @Inject
    public ContestWebResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestWebConfigFetcher webConfigFetcher) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.webConfigFetcher = webConfigFetcher;
    }

    @Override
    @UnitOfWork
    public ContestWebConfig getConfig(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        return webConfigFetcher.fetchConfig(actorJid, contest);
    }
}
