package judgels.catalog.problem.version;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import judgels.api.catalog.CatalogVersion;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.version.ProblemVersionCommitData;
import judgels.api.catalog.problem.version.ProblemVersionsResponse;
import judgels.catalog.CatalogVersions;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;

@Path("/api/v4/problems/{problemJid}/versions")
public class ProblemVersionResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemVersionStore versionStore;
    @Inject protected ProblemVersionService versionService;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemVersionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemVersionsResponse getVersions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        checkCanEdit(authHeader, problemJid);

        List<CatalogVersion> versions = CatalogVersions.fromGitCommits(versionStore.getVersions(null, problemJid));

        var userJids = Lists.transform(versions, CatalogVersion::getUserJid);

        return new ProblemVersionsResponse.Builder()
                .data(versions)
                .profilesMap(profileStore.getProfiles(userJids))
                .build();
    }

    @POST
    @Path("/{versionHash}/restore")
    @UnitOfWork
    public void restoreVersion(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("versionHash") String versionHash) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        versionService.restoreVersion(actorJid, problemJid, versionHash);
    }

    @POST
    @Path("/local/commit")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void commitVersionLocalChanges(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            ProblemVersionCommitData data) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        versionService.commitLocalChanges(actorJid, problemJid, data.getTitle(), data.getDescription());
    }

    @POST
    @Path("/local/rebase")
    @UnitOfWork
    public void rebaseVersionLocalChanges(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        versionService.rebaseLocalChanges(actorJid, problemJid);
    }

    @DELETE
    @Path("/local")
    @UnitOfWork
    public void discardVersionLocalChanges(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        versionService.discardLocalChanges(actorJid, problemJid);
    }

    // Unlike the endpoints that write a problem's files, these act on the actor's clone as it is,
    // so they must not create one.
    private String checkCanEdit(AuthHeader authHeader, String problemJid) {
        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canEdit(actorJid, problem));
        return actorJid;
    }
}
