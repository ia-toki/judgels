package judgels.catalog.problem.writer;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;

import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import judgels.api.catalog.problem.ProblemErrors;
import judgels.api.catalog.problem.writer.ProblemWriterData;
import judgels.api.catalog.problem.writer.ProblemWritersResponse;
import judgels.api.user.role.UserRole;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.user.UserStore;
import judgels.user.role.UserRoleStore;

@Path("/api/v4/problems/writers")
public class ProblemWriterResource {
    private static final int MAX_WRITERS = 5000;
    private static final Set<UserRole> WRITER_SCOPE = Set.of(UserRole.PROBLEM_WRITER);

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected UserRoleStore userRoleStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;

    @Inject public ProblemWriterResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemWritersResponse getWriters(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        List<String> writerJids = userRoleStore.getUserJidsByRole(UserRole.PROBLEM_WRITER);

        return new ProblemWritersResponse.Builder()
                .data(writerJids)
                .profilesMap(profileStore.getProfiles(writerJids))
                .build();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setWriters(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, List<ProblemWriterData> data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> usernames = data.stream().map(ProblemWriterData::getUsername).collect(Collectors.toSet());
        if (usernames.size() != data.size()) {
            throw new BadRequestException();
        }
        if (data.size() > MAX_WRITERS) {
            throw new BadRequestException();
        }

        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(usernames);

        Set<String> usernamesNotFound = Sets.difference(usernames, usernameToJidMap.keySet());
        if (!usernamesNotFound.isEmpty()) {
            throw ProblemErrors.writerUsernamesNotFound(new TreeSet<>(usernamesNotFound));
        }

        Set<String> newWriterJids = new HashSet<>(usernameToJidMap.values());
        Set<String> oldWriterJids = new HashSet<>(userRoleStore.getUserJidsByRole(UserRole.PROBLEM_WRITER));

        for (String userJid : Sets.difference(oldWriterJids, newWriterJids)) {
            userRoleStore.setRoles(userJid, Set.of(), WRITER_SCOPE);
        }
        for (String userJid : Sets.difference(newWriterJids, oldWriterJids)) {
            userRoleStore.addRoles(userJid, WRITER_SCOPE);
        }
    }
}
