package judgels.catalog.problem.setter;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemErrors;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.api.catalog.problem.setter.ProblemSettersResponse;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.ProblemUpdater;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.user.UserStore;

@Path("/api/v4/problems/{problemJid}/setters")
public class ProblemSetterResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemUpdater problemUpdater;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;

    @Inject public ProblemSetterResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSettersResponse getSetters(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canView(actorJid, problem));

        Map<ProblemSetterRole, List<String>> setterJidsMap = problemStore.getProblemSetters(problemJid);

        Set<String> userJids = new HashSet<>();
        setterJidsMap.values().forEach(userJids::addAll);

        return new ProblemSettersResponse.Builder()
                .data(setterJidsMap)
                .profilesMap(profileStore.getProfiles(userJids))
                .build();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setSetters(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            Map<ProblemSetterRole, List<String>> setterUsernamesMap) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        Set<String> usernames = new HashSet<>();
        setterUsernamesMap.values().forEach(usernames::addAll);
        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(usernames);

        Set<String> usernamesNotFound = Sets.difference(usernames, usernameToJidMap.keySet());
        if (!usernamesNotFound.isEmpty()) {
            throw ProblemErrors.setterUsernamesNotFound(new TreeSet<>(usernamesNotFound));
        }

        Map<ProblemSetterRole, List<String>> setterJidsMap = new HashMap<>();
        setterUsernamesMap.forEach((role, setterUsernames) -> setterJidsMap.put(
                role,
                setterUsernames.stream().map(usernameToJidMap::get).collect(Collectors.toList())));

        problemUpdater.updateSetters(problemJid, setterJidsMap);
    }
}
