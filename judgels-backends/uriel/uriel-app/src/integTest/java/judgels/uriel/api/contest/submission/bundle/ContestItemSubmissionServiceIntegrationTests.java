package judgels.uriel.api.contest.submission.bundle;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.ItemSubmissionData;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestItemSubmissionServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private static final String PROBLEM_3_ALIAS = "B";
    private final ContestItemSubmissionService submissionService = createService(ContestItemSubmissionService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .problems(PROBLEM_3_ALIAS, PROBLEM_3_SLUG)
                .build();
    }

    // CHECKSTYLE.OFF: MethodLengthCheck
    @Test
    // CHECKSTYLE.ON: MethodLengthCheck
    void submit_get_submissions() {
        submissionService.createItemSubmission(CONTESTANT_HEADER, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMtOoiXuIgPcD1oUsMzvbP")
                .answer("b")
                .build()
        );

        Map<String, ItemSubmission> answersMap;
        ItemSubmission itemSubmissionResult;
        ContestSubmissionSummaryResponse summaryResult;
        ContestItemSubmissionsResponse submissionsResponse;

        submissionsResponse = submissionService.getSubmissions(
                SUPERVISOR_HEADER, contest.getJid(), Optional.empty(), Optional.of("invalid-alias"), Optional.of(1));

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isTrue();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();
        assertThat(submissionsResponse.getData().getPage()).hasSize(0);

        submissionsResponse = submissionService.getSubmissions(
                SUPERVISOR_HEADER, contest.getJid(), Optional.of("invalid-username"), Optional.empty(), Optional.of(1));

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isTrue();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();
        assertThat(submissionsResponse.getData().getPage()).hasSize(0);

        submissionsResponse = submissionService.getSubmissions(
                CONTESTANT_HEADER, contest.getJid(), Optional.empty(), Optional.of(PROBLEM_3_ALIAS), Optional.of(1));

        assertThat(submissionsResponse.getProblemAliasesMap()).hasSize(1);
        assertThat(submissionsResponse.getProblemAliasesMap()).containsKey("problemJid3");
        assertThat(submissionsResponse.getProblemAliasesMap().get("problemJid3")).isEqualTo(PROBLEM_3_ALIAS);

        assertThat(submissionsResponse.getProfilesMap()).hasSize(1);
        assertThat(submissionsResponse.getProfilesMap()).containsKey(CONTESTANT_JID);
        assertThat(submissionsResponse.getProfilesMap().get(CONTESTANT_JID).getUsername()).isEqualTo("contestant");

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isFalse();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();

        assertThat(submissionsResponse.getItemNumbersMap()).hasSize(1);
        assertThat(submissionsResponse.getItemNumbersMap()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(submissionsResponse.getItemNumbersMap().get("JIDITEMtOoiXuIgPcD1oUsMzvbP")).isEqualTo(2);

        assertThat(submissionsResponse.getItemTypesMap()).hasSize(1);
        assertThat(submissionsResponse.getItemTypesMap()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(submissionsResponse.getItemTypesMap().get("JIDITEMtOoiXuIgPcD1oUsMzvbP"))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);

        assertThat(submissionsResponse.getData().getPage()).hasSize(1);

        itemSubmissionResult = submissionsResponse.getData().getPage().get(0);
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(PROBLEM_3_JID);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        answersMap = submissionService.getLatestSubmissions(
                CONTESTANT_HEADER,
                contest.getJid(),
                Optional.of(ADMIN), // Contestant should not be able to see other users' answers
                PROBLEM_3_ALIAS
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

        answersMap = submissionService.getLatestSubmissions(
                SUPERVISOR_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT),
                PROBLEM_3_ALIAS
        );

        assertThat(answersMap).hasSize(1);
        assertThat(answersMap).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");

        summaryResult = submissionService.getSubmissionSummary(
                CONTESTANT_HEADER,
                contest.getJid(),
                Optional.of(ADMIN), // Contestant should not be able to see other users' answers
                Optional.empty()
        );

        assertThat(summaryResult.getProfile().getUsername()).isEqualTo("contestant");

        assertThat(summaryResult.getProblemAliasesMap()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemAliasesMap().get("problemJid3")).isEqualTo(PROBLEM_3_ALIAS);

        assertThat(summaryResult.getProblemNamesMap()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemNamesMap().get("problemJid3")).isEqualTo("Problem 3");

        assertThat(summaryResult.getConfig().getCanSupervise()).isFalse();
        assertThat(summaryResult.getConfig().getCanManage()).isFalse();

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();

        assertThat(summaryResult.getItemTypesMap()).hasSize(4);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMPeKuqUA0Q7zvJjTQXXVD"))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMtOoiXuIgPcD1oUsMzvbP"))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMcD1oSDFJLadFSsMddfsf");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMcD1oSDFJLadFSsMddfsf"))
                .isEqualTo(ItemType.SHORT_ANSWER);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMkhUulUkbUkYGBKYkfLHUh");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMkhUulUkbUkYGBKYkfLHUh"))
                .isEqualTo(ItemType.ESSAY);

        assertThat(summaryResult.getItemJidsByProblemJid()).hasSize(1);
        assertThat(summaryResult.getItemJidsByProblemJid()).isEqualTo(
                ImmutableMap.of(
                    "problemJid3", ImmutableList.of(
                                "JIDITEMPeKuqUA0Q7zvJjTQXXVD",
                                "JIDITEMtOoiXuIgPcD1oUsMzvbP",
                                "JIDITEMcD1oSDFJLadFSsMddfsf",
                                "JIDITEMkhUulUkbUkYGBKYkfLHUh"
                        )
                )
        );

        summaryResult = submissionService.getSubmissionSummary(
                MANAGER_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT),
                Optional.empty()
        );

        assertThat(summaryResult.getProfile().getUsername()).isEqualTo("contestant");

        assertThat(summaryResult.getProblemAliasesMap()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemAliasesMap().get("problemJid3")).isEqualTo(PROBLEM_3_ALIAS);

        assertThat(summaryResult.getProblemNamesMap()).containsKey("problemJid3");
        assertThat(summaryResult.getProblemNamesMap().get("problemJid3")).isEqualTo("Problem 3");

        assertThat(summaryResult.getConfig().getCanSupervise()).isTrue();
        assertThat(summaryResult.getConfig().getCanManage()).isTrue();

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(-1).build());

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMtOoiXuIgPcD1oUsMzvbP")
                .answer("a")
                .build()
        );

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMPeKuqUA0Q7zvJjTQXXVD")
                .answer("a")
                .build()
        );

        answersMap = submissionService.getLatestSubmissions(
                CONTESTANT_HEADER,
                contest.getJid(),
                Optional.empty(),
                PROBLEM_3_ALIAS
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

        summaryResult = submissionService.getSubmissionSummary(
                MANAGER_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT),
                Optional.empty()
        );

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(2);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4).build());

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMkhUulUkbUkYGBKYkfLHUh")
                .answer("print('hello world!')")
                .build()
        );

        submissionService.createItemSubmission(CONTESTANT_HEADER, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(PROBLEM_3_JID)
                .itemJid("JIDITEMcD1oSDFJLadFSsMddfsf")
                .answer("123")
                .build()
        );

        summaryResult = submissionService.getSubmissionSummary(
                MANAGER_HEADER,
                contest.getJid(),
                Optional.of(CONTESTANT),
                Optional.empty()
        );

        assertThat(summaryResult.getItemTypesMap()).hasSize(4);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMPeKuqUA0Q7zvJjTQXXVD"))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMtOoiXuIgPcD1oUsMzvbP"))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMcD1oSDFJLadFSsMddfsf");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMcD1oSDFJLadFSsMddfsf"))
                .isEqualTo(ItemType.SHORT_ANSWER);
        assertThat(summaryResult.getItemTypesMap()).containsKey("JIDITEMkhUulUkbUkYGBKYkfLHUh");
        assertThat(summaryResult.getItemTypesMap().get("JIDITEMkhUulUkbUkYGBKYkfLHUh"))
                .isEqualTo(ItemType.ESSAY);

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(4);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMcD1oSDFJLadFSsMddfsf");
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey("JIDITEMkhUulUkbUkYGBKYkfLHUh");

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMPeKuqUA0Q7zvJjTQXXVD");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(0.0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMtOoiXuIgPcD1oUsMzvbP");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4.0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMcD1oSDFJLadFSsMddfsf");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMcD1oSDFJLadFSsMddfsf");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("123");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4.0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get("JIDITEMkhUulUkbUkYGBKYkfLHUh");
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo("JIDITEMkhUulUkbUkYGBKYkfLHUh");
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("print('hello world!')");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.PENDING_MANUAL_GRADING).build());
    }
}
