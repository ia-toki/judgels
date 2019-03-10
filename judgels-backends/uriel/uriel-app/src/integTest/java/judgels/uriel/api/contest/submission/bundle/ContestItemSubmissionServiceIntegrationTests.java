package judgels.uriel.api.contest.submission.bundle;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import org.junit.jupiter.api.Test;

class ContestItemSubmissionServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestProblemService problemService = createService(ContestProblemService.class);
    private ContestItemSubmissionService submissionService = createService(ContestItemSubmissionService.class);

    // CHECKSTYLE.OFF: MethodLengthCheck
    @Test
    // CHECKSTYLE.ON: MethodLengthCheck
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_3_SLUG)
                        .status(ContestProblemStatus.OPEN)
                        .submissionsLimit(0)
                        .build()));

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ContestItemSubmissionData.Builder()
                .contestJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMtOoiXuIgPcD1oUsMzvbP")
                .answer("b")
                .build()
        );

        Map<String, ItemSubmission> answersMap;
        ItemSubmission itemSubmissionResult;
        ContestantAnswerSummaryResponse summaryResult;
        List<ItemSubmission> problemSubmissions;
        ContestItemSubmissionsResponse submissionsResponse;

        submissionsResponse = submissionService.getSubmissions(
                CONTESTANT_HEADER, contest.getJid(), Optional.empty(), Optional.of(PROBLEM_3_JID), Optional.of(1));

        assertThat(submissionsResponse.getProblemAliasesMap()).hasSize(1);
        assertThat(submissionsResponse.getProblemAliasesMap()).containsKey("problemJid3");
        assertThat(submissionsResponse.getProblemAliasesMap().get("problemJid3")).isEqualTo("B");

        assertThat(submissionsResponse.getProfilesMap()).hasSize(1);
        assertThat(submissionsResponse.getProfilesMap()).containsKey(CONTESTANT_JID);
        assertThat(submissionsResponse.getProfilesMap().get(CONTESTANT_JID).getUsername()).isEqualTo("contestant");

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isFalse();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();

        assertThat(submissionsResponse.getData().getPage()).hasSize(1);

        itemSubmissionResult = submissionsResponse.getData().getPage().get(0);
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(PROBLEM_3_JID);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        answersMap = submissionService.getLatestSubmissionsByUserForProblemInContest(
                CONTESTANT_HEADER,
                contest.getJid(),
                Optional.of(ADMIN_JID), // Contestant should not be able to see other users' answers
                PROBLEM_3_JID
        );

        assertThat(answersMap).hasSize(1);
        assertThat(answersMap).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");

        itemSubmissionResult = answersMap.get("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(PROBLEM_3_JID);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        answersMap = submissionService.getLatestSubmissionsByUserForProblemInContest(
                SUPERVISOR_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT_JID),
                PROBLEM_3_JID
        );

        assertThat(answersMap).hasSize(1);
        assertThat(answersMap).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");

        summaryResult = submissionService.getAnswerSummaryForContestant(
                CONTESTANT_HEADER,
                contest.getJid(),
                Optional.of(ADMIN_JID), // Contestant should not be able to see other users' answers
                Optional.empty()
        );

        assertThat(summaryResult.getProfile().getUsername()).isEqualTo("contestant");

        assertThat(summaryResult.getItemNumberByItemJid()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(summaryResult.getItemNumberByItemJid().get("JIDITEMtOoiXuIgPcD1oUsMzvbP")).isEqualTo(2);

        assertThat(summaryResult.getProblemAliasesByProblemJid()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemAliasesByProblemJid().get("problemJid3")).isEqualTo("B");

        assertThat(summaryResult.getProblemNamesByProblemJid()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemNamesByProblemJid().get("problemJid3")).isEqualTo("Problem 3");

        assertThat(summaryResult.getConfig().getCanSupervise()).isFalse();
        assertThat(summaryResult.getConfig().getCanManage()).isFalse();

        assertThat(summaryResult.getSubmissionsByProblemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByProblemJid()).containsKey("problemJid3");

        problemSubmissions = summaryResult.getSubmissionsByProblemJid().get("problemJid3");
        assertThat(problemSubmissions).hasSize(2);

        assertThat(problemSubmissions.get(0).getItemJid()).isEqualTo("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(problemSubmissions.get(0).getJid()).isEmpty();
        assertThat(problemSubmissions.get(0).getAnswer()).isEmpty();
        assertThat(problemSubmissions.get(0).getGrading()).isEmpty();

        assertThat(problemSubmissions.get(1).getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(problemSubmissions.get(1).getJid()).isNotEmpty();
        assertThat(problemSubmissions.get(1).getAnswer()).isEqualTo("b");
        assertThat(problemSubmissions.get(1).getGrading()).isEmpty();

        summaryResult = submissionService.getAnswerSummaryForContestant(
                MANAGER_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT_JID),
                Optional.empty()
        );

        assertThat(summaryResult.getProfile().getUsername()).isEqualTo("contestant");

        assertThat(summaryResult.getItemNumberByItemJid()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(summaryResult.getItemNumberByItemJid().get("JIDITEMtOoiXuIgPcD1oUsMzvbP")).isEqualTo(2);

        assertThat(summaryResult.getProblemAliasesByProblemJid()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemAliasesByProblemJid().get("problemJid3")).isEqualTo("B");

        assertThat(summaryResult.getProblemNamesByProblemJid()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemNamesByProblemJid().get("problemJid3")).isEqualTo("Problem 3");

        assertThat(summaryResult.getConfig().getCanSupervise()).isTrue();
        assertThat(summaryResult.getConfig().getCanManage()).isTrue();

        assertThat(summaryResult.getSubmissionsByProblemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByProblemJid()).containsKey("problemJid3");

        problemSubmissions = summaryResult.getSubmissionsByProblemJid().get("problemJid3");
        assertThat(problemSubmissions).hasSize(2);

        assertThat(problemSubmissions.get(0).getItemJid()).isEqualTo("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(problemSubmissions.get(0).getJid()).isEmpty();
        assertThat(problemSubmissions.get(0).getAnswer()).isEmpty();
        assertThat(problemSubmissions.get(0).getGrading()).isEmpty();

        assertThat(problemSubmissions.get(1).getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(problemSubmissions.get(1).getJid()).isNotEmpty();
        assertThat(problemSubmissions.get(1).getAnswer()).isEqualTo("b");
        assertThat(problemSubmissions.get(1).getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(-1).build());

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ContestItemSubmissionData.Builder()
                .contestJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMtOoiXuIgPcD1oUsMzvbP")
                .answer("a")
                .build()
        );

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ContestItemSubmissionData.Builder()
                .contestJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMPeKuqUA0Q7zvJjTQXXVD")
                .answer("a")
                .build()
        );

        answersMap = submissionService.getLatestSubmissionsByUserForProblemInContest(
                CONTESTANT_HEADER,
                contest.getJid(),
                Optional.empty(),
                PROBLEM_3_JID
        );

        assertThat(answersMap).hasSize(2);
        assertThat(answersMap).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(answersMap).containsKey("JIDITEMPeKuqUA0Q7zvJjTQXXVD");

        itemSubmissionResult = answersMap.get("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(PROBLEM_3_JID);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        itemSubmissionResult = answersMap.get("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(PROBLEM_3_JID);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        summaryResult = submissionService.getAnswerSummaryForContestant(
                MANAGER_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT_JID),
                Optional.empty()
        );

        assertThat(summaryResult.getSubmissionsByProblemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByProblemJid()).containsKey("problemJid3");

        problemSubmissions = summaryResult.getSubmissionsByProblemJid().get("problemJid3");
        assertThat(problemSubmissions).hasSize(2);

        assertThat(problemSubmissions.get(0).getItemJid()).isEqualTo("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(problemSubmissions.get(0).getJid()).isNotEmpty();
        assertThat(problemSubmissions.get(0).getAnswer()).isEqualTo("a");
        assertThat(problemSubmissions.get(0).getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(0).build());

        assertThat(problemSubmissions.get(1).getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(problemSubmissions.get(1).getJid()).isNotEmpty();
        assertThat(problemSubmissions.get(1).getAnswer()).isEqualTo("a");
        assertThat(problemSubmissions.get(1).getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4).build());
    }
}
