package judgels.uriel.contest.web;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.api.contest.web.ContestWithWebConfig;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;

@Path("/api/v2/contest-web")
public class ContestWebResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestWebConfigFetcher webConfigFetcher;

    @Inject public ContestWebResource() {}

    @GET
    @Path("/slug/{contestSlug}/with-config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestWithWebConfig getContestBySlugWithWebConfig(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestSlug") String contestSlug) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return new ContestWithWebConfig.Builder()
                .contest(contest)
                .config(webConfigFetcher.fetchConfig(actorJid, contest))
                .build();
    }

    @GET
    @Path("/{contestJid}/with-config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestWithWebConfig getContestByJidWithWebConfig(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return new ContestWithWebConfig.Builder()
                .contest(contest)
                .config(webConfigFetcher.fetchConfig(actorJid, contest))
                .build();
    }

    @GET
    @Path("/{contestJid}/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestWebConfig getWebConfig(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return webConfigFetcher.fetchConfig(actorJid, contest);
    }

    @GET
    @Path("/slug/{contestSlug}/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestWebConfig getWebConfigBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestSlug") String contestSlug) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return webConfigFetcher.fetchConfig(actorJid, contest);
    }
}
