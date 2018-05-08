package judgels.uriel.contest.submission;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.Submission;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.submission.ContestSubmissionService;
import judgels.uriel.role.RoleChecker;

public class ContestSubmissionResource implements ContestSubmissionService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestSubmissionStore submissionStore;

    @Inject
    public ContestSubmissionResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestSubmissionStore submissionStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.submissionStore = submissionStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Submission> getMySubmissions(AuthHeader authHeader, String contestJid, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewOwnSubmissions(actorJid, contestJid));

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);

        return submissionStore.getSubmissions(contestJid, actorJid, options.build());
    }
}
