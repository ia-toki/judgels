package judgels.uriel.api.contest.module;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/{contestJid}/modules")
public interface ContestModuleService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    Set<ContestModuleType> getModules(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

    @PUT
    @Path("/{type}")
    void enableModule(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("type") ContestModuleType type);

    @DELETE
    @Path("/{type}")
    void disableModule(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("type") ContestModuleType type);

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    ContestModulesConfig getConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

    @PUT
    @Path("/config")
    @Consumes(APPLICATION_JSON)
    void upsertConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestModulesConfig config);
}
