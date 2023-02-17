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
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.profile.ProfileStore;
import judgels.michael.MichaelConfiguration;
import judgels.michael.actor.Actor;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemSearchStore;

@Path("/problems")
public class ProblemResource extends BaseProblemResource {
    private final ActorChecker actorChecker;
    private final ProblemSearchStore problemSearchStore;
    private final ProfileStore profileStore;

    @Inject
    public ProblemResource(
            MichaelConfiguration config,
            ActorChecker actorChecker,
            ProblemSearchStore problemSearchStore,
            ProfileStore profileStore) {

        super(config);
        this.actorChecker = actorChecker;
        this.problemSearchStore = problemSearchStore;
        this.profileStore = profileStore;
    }

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        boolean isAdmin = true; // TODO(fushar): later
        boolean isWriter = true; // TODO(fushar): later

        Page<Problem> problems = problemSearchStore.searchProblems(1, "updatedAt", "desc", "", null, actor.getUserJid(), isAdmin);
        Set<String> userJids = problems.getPage().stream().map(Problem::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newTemplate(actor);
        template.setTitle("Problems");
        return new ListProblemsView(template, problems, profilesMap);
    }
}
