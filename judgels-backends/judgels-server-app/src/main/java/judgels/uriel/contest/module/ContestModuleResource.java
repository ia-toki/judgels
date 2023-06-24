package judgels.uriel.contest.module;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.jophiel.JophielClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

@Path("/api/v2/contests/{contestJid}/modules")
public class ContestModuleResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestModuleStore moduleStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestModuleResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Set<ContestModuleType> getModules(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));

        contestLogger.log(contestJid, "OPEN_MODULES");

        return moduleStore.getEnabledModules(contest.getJid());
    }

    @PUT
    @Path("/{type}")
    @UnitOfWork
    public void enableModule(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("type") ContestModuleType type) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        moduleStore.enableModule(contest.getJid(), type);

        contestLogger.log(contestJid, "ENABLE_MODULE", type.name());
    }

    @DELETE
    @Path("/{type}")
    @UnitOfWork
    public void disableModule(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("type") ContestModuleType type) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        moduleStore.disableModule(contest.getJid(), type);

        contestLogger.log(contestJid, "DISABLE_MODULE", type.name());
    }

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestModulesConfig getConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canSupervise(actorJid, contest));
        ContestModulesConfig config = moduleStore.getConfig(contest.getJid(), contest.getStyle());
        if (config.getEditorial().isPresent()) {
            config = new ContestModulesConfig.Builder()
                    .from(config)
                    .profilesMap(jophielClient.parseProfiles(config.getEditorial().get().getPreface().orElse("")))
                    .build();
        }
        return config;
    }

    @PUT
    @Path("/config")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void upsertConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestModulesConfig config) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        moduleStore.upsertConfig(contest.getJid(), config);

        contestLogger.log(contestJid, "UPDATE_MODULE_CONFIG");
    }
}
