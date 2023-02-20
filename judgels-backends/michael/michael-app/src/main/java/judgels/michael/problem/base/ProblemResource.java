package judgels.michael.problem.base;

import static java.util.stream.Collectors.toSet;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.profile.ProfileStore;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemSearchStore;
import judgels.sandalphon.role.RoleChecker;

@Path("/problems")
public class ProblemResource extends BaseProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected ProblemSearchStore problemSearchStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Page<Problem> problems = problemSearchStore.searchProblems(1, "updatedAt", "desc", "", null, actor.getUserJid(), isAdmin);
        Set<String> userJids = problems.getPage().stream().map(Problem::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newTemplate(actor);
        template.setTitle("Problems");
        return new ListProblemsView(template, problems, profilesMap);
    }
}
