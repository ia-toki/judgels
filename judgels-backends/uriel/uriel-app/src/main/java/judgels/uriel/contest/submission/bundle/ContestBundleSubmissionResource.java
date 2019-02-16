package judgels.uriel.contest.submission.bundle;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.BundleItemSubmission;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.submission.bundle.ContestBundleSubmissionData;
import judgels.uriel.api.contest.submission.bundle.ContestBundleSubmissionService;
import judgels.uriel.api.contest.submission.bundle.ContestBundleSubmissionsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;

public class ContestBundleSubmissionResource implements ContestBundleSubmissionService {

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestBundleSubmissionStore submissionStore;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestContestantStore contestantStore;
    private final ContestProblemStore problemStore;
    private final ProfileService profileService;

    @Inject
    public ContestBundleSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestBundleSubmissionStore submissionStore,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.submissionStore = submissionStore;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork
    public ContestBundleSubmissionsResponse getSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        Optional<String> actualUserJid = canSupervise ? userJid : Optional.of(actorJid);

        Page<BundleItemSubmission> submissions =
                submissionStore.getSubmissions(contest.getJid(), actualUserJid, problemJid, page);

        List<String> userJidsSortedByUsername;
        Set<String> userJids;

        List<String> problemJidsSortedByAlias;
        Set<String> problemJids;

        userJids = submissions.getPage().stream().map(BundleItemSubmission::getUserJid).collect(Collectors.toSet());
        if (canSupervise) {
            userJids.addAll(contestantStore.getApprovedContestantJids(contestJid));
            userJidsSortedByUsername = Lists.newArrayList(userJids);

            problemJidsSortedByAlias = problemStore.getProblemJids(contestJid);
            problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        } else {
            userJidsSortedByUsername = Collections.emptyList();

            problemJidsSortedByAlias = Collections.emptyList();
            problemJids = submissions.getPage().stream()
                    .map(BundleItemSubmission::getProblemJid)
                    .collect(Collectors.toSet());
        }

        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

        userJidsSortedByUsername.sort((u1, u2) -> {
            String usernameA = profilesMap.containsKey(u1) ? profilesMap.get(u1).getUsername() : u1;
            String usernameB = profilesMap.containsKey(u2) ? profilesMap.get(u2).getUsername() : u2;
            return usernameA.compareTo(usernameB);
        });

        ContestSubmissionConfig config = new ContestSubmissionConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(submissionRoleChecker.canManage(actorJid, contest))
                .userJids(userJidsSortedByUsername)
                .problemJids(problemJidsSortedByAlias)
                .build();

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        return new ContestBundleSubmissionsResponse.Builder()
                .data(submissions)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public void createSubmission(AuthHeader authHeader, ContestBundleSubmissionData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(data.getContestJid()));
        ContestProblem problem = checkFound(problemStore.getProblem(data.getContestJid(), data.getProblemJid()));
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, 0));

        submissionStore.upsertSubmission(data, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, BundleItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            AuthHeader authHeader,
            String contestJid,
            String problemJid,
            Optional<String> userJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String actualUserJid = canSupervise ? userJid.orElse(actorJid) : actorJid;

        Map<String, BundleItemSubmission> submissionsByItemJid = submissionStore
                .getLatestSubmissionsByUserForProblemInContest(contestJid, problemJid, actualUserJid);
        return submissionsByItemJid;
    }
}
