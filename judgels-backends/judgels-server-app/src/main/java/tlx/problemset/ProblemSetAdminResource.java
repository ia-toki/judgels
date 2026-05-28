package tlx.problemset;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.profile.Profile;
import judgels.archive.ArchiveStore;
import judgels.persistence.api.Page;
import judgels.problemset.ProblemSetStore;
import judgels.profile.ProfileStore;
import judgels.role.TrainingAdminRoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.stats.StatsStore;
import tlx.api.archive.Archive;
import tlx.api.problemset.ProblemSet;
import tlx.api.problemset.ProblemSetCreateData;
import tlx.api.problemset.ProblemSetProgress;
import tlx.api.problemset.ProblemSetUpdateData;
import tlx.api.problemset.ProblemSetsResponse;

@Path("/api/v2/admin/problemsets")
public class ProblemSetAdminResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
    @Inject protected ProblemSetStore problemSetStore;
    @Inject protected ArchiveStore archiveStore;
    @Inject protected StatsStore statsStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemSetAdminResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSetsResponse getProblemSets(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("archiveSlug") Optional<String> archiveSlug,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        Optional<Archive> archive = archiveSlug.flatMap(archiveStore::getArchiveBySlug);
        Optional<String> archiveJid = archiveSlug.isPresent()
                ? Optional.of(archive.map(Archive::getJid).orElse(""))
                : Optional.empty();

        Page<ProblemSet> problemSets = problemSetStore.getProblemSets(archiveJid, name, pageNumber, PAGE_SIZE);

        var archiveJids = Lists.transform(problemSets.getPage(), ProblemSet::getArchiveJid);
        Map<String, Archive> archivesMap = archiveStore.getArchivesByJids(archiveJids);
        Map<String, String> archiveSlugsMap = archivesMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getSlug()));
        Map<String, String> archiveDescriptionsMap = archivesMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDescription()));

        var problemSetJids = Lists.transform(problemSets.getPage(), ProblemSet::getJid);
        Map<String, ProblemSetProgress> problemSetProgressesMap =
                statsStore.getProblemSetProgressesMap(actorJid, problemSetJids);

        String descriptions = "";
        for (String description : archiveDescriptionsMap.values()) {
            descriptions = descriptions.concat(description);
        }
        for (ProblemSet problemSet : problemSets.getPage()) {
            descriptions = descriptions.concat(problemSet.getDescription());
        }
        Map<String, Profile> profilesMap = profileStore.parseProfiles(descriptions);

        return new ProblemSetsResponse.Builder()
                .data(problemSets)
                .archiveSlugsMap(archiveSlugsMap)
                .archiveDescriptionsMap(archiveDescriptionsMap)
                .archiveName(archive.map(Archive::getName))
                .problemSetProgressesMap(problemSetProgressesMap)
                .profilesMap(profilesMap)
                .build();
    }

    @GET
    @Path("/slug/{problemSetSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSet getProblemSetBySlug(@PathParam("problemSetSlug") String problemSetSlug) {
        return checkFound(problemSetStore.getProblemSetBySlug(problemSetSlug));
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ProblemSet createProblemSet(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ProblemSetCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return problemSetStore.createProblemSet(data);
    }

    @POST
    @Path("/{problemSetJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ProblemSet updateProblemSet(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            ProblemSetUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        return problemSetStore.updateProblemSet(problemSetJid, data);
    }
}
