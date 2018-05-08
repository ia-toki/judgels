package judgels.uriel.contest.web;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.api.contest.web.ContestWebService;

public class ContestWebResource implements ContestWebService {
    private final ActorChecker actorChecker;
    private final ContestWebConfigFetcher webConfigFetcher;

    @Inject
    public ContestWebResource(ActorChecker actorChecker, ContestWebConfigFetcher webConfigFetcher) {
        this.actorChecker = actorChecker;
        this.webConfigFetcher = webConfigFetcher;
    }

    @Override
    @UnitOfWork
    public ContestWebConfig getConfig(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        return webConfigFetcher.getConfig(actorJid, contestJid);
    }
}
