package judgels.uriel.api.contest.submission.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.core.Form;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.ItemSubmissionData;
import judgels.sandalphon.api.submission.bundle.Verdict;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestItemSubmissionServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
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
        updateProblemStatement(managerHeader, problem3, "Problem 3", "text");

        Form form = new Form();
        form.param("meta", "1-2");
        form.param("statement", "<p>STATEMENT 1-2</p>");
        String item1Jid = createBundleProblemItem(managerHeader, problem3, ItemType.STATEMENT, form);

        form = new Form();
        form.param("meta", "1");
        form.param("statement", "<p>QUESTION 1</p>");
        form.param("score", "1");
        form.param("penalty", "0");
        form.param("choiceAliases", "a");
        form.param("choiceContents", "answer a");
        form.param("choiceAliases", "b");
        form.param("choiceContents", "answer b");
        form.param("choiceIsCorrects", "1");
        String item2Jid = createBundleProblemItem(managerHeader, problem3, ItemType.MULTIPLE_CHOICE, form);

        form = new Form();
        form.param("meta", "2");
        form.param("statement", "<p>QUESTION 2</p>");
        form.param("score", "4");
        form.param("penalty", "-1");
        form.param("choiceAliases", "a");
        form.param("choiceContents", "answer a");
        form.param("choiceIsCorrects", "0");
        form.param("choiceAliases", "b");
        form.param("choiceContents", "answer b");
        String item3Jid = createBundleProblemItem(managerHeader, problem3, ItemType.MULTIPLE_CHOICE, form);

        form = new Form();
        form.param("meta", "3");
        form.param("statement", "<p>QUESTION 3</p>");
        form.param("score", "4");
        form.param("penalty", "-1");
        form.param("inputValidationRegex", "\\d+");
        form.param("gradingRegex", "123");
        String item4Jid = createBundleProblemItem(managerHeader, problem3, ItemType.SHORT_ANSWER, form);

        form = new Form();
        form.param("meta", "4");
        form.param("statement", "<p>QUESTION 4</p>");
        form.param("score", "12");
        String item5Jid = createBundleProblemItem(managerHeader, problem3, ItemType.ESSAY, form);

        submissionService.createItemSubmission(contestantHeader, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item3Jid)
                .answer("b")
                .build()
        );

        Map<String, ItemSubmission> answersMap;
        ItemSubmission itemSubmissionResult;
        ContestSubmissionSummaryResponse summaryResult;
        ContestItemSubmissionsResponse submissionsResponse;

        submissionsResponse = submissionService.getSubmissions(
                supervisorHeader, contest.getJid(), Optional.empty(), Optional.of("invalid-alias"), Optional.of(1));

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isTrue();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();
        assertThat(submissionsResponse.getData().getPage()).hasSize(0);

        submissionsResponse = submissionService.getSubmissions(
                supervisorHeader, contest.getJid(), Optional.of("invalid-username"), Optional.empty(), Optional.of(1));

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isTrue();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();
        assertThat(submissionsResponse.getData().getPage()).hasSize(0);

        submissionsResponse = submissionService.getSubmissions(
                contestantHeader, contest.getJid(), Optional.empty(), Optional.of(PROBLEM_3_ALIAS), Optional.of(1));

        assertThat(submissionsResponse.getProblemAliasesMap()).hasSize(1);
        assertThat(submissionsResponse.getProblemAliasesMap()).containsKey(problem3.getJid());
        assertThat(submissionsResponse.getProblemAliasesMap().get(problem3.getJid())).isEqualTo(PROBLEM_3_ALIAS);

        assertThat(submissionsResponse.getProfilesMap()).hasSize(1);
        assertThat(submissionsResponse.getProfilesMap()).containsKey(contestant.getJid());
        assertThat(submissionsResponse.getProfilesMap().get(contestant.getJid()).getUsername()).isEqualTo("contestant");

        assertThat(submissionsResponse.getConfig().getCanSupervise()).isFalse();
        assertThat(submissionsResponse.getConfig().getCanManage()).isFalse();

        assertThat(submissionsResponse.getItemNumbersMap()).hasSize(1);
        assertThat(submissionsResponse.getItemNumbersMap()).containsKey(item3Jid);
        assertThat(submissionsResponse.getItemNumbersMap().get(item3Jid)).isEqualTo(2);

        assertThat(submissionsResponse.getItemTypesMap()).hasSize(1);
        assertThat(submissionsResponse.getItemTypesMap()).containsKey(item3Jid);
        assertThat(submissionsResponse.getItemTypesMap().get(item3Jid))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);

        assertThat(submissionsResponse.getData().getPage()).hasSize(1);

        itemSubmissionResult = submissionsResponse.getData().getPage().get(0);
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(problem3.getJid());
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        answersMap = submissionService.getLatestSubmissions(
                contestantHeader,
                contest.getJid(),
                Optional.of(ADMIN), // Contestant should not be able to see other users' answers
                PROBLEM_3_ALIAS
        );

        assertThat(answersMap).hasSize(1);
        assertThat(answersMap).containsKey(item3Jid);

        itemSubmissionResult = answersMap.get(item3Jid);
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(problem3.getJid());
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        answersMap = submissionService.getLatestSubmissions(
                supervisorHeader,
                contest.getJid(),
                Optional.of(CONTESTANT),
                PROBLEM_3_ALIAS
        );

        assertThat(answersMap).hasSize(1);
        assertThat(answersMap).containsKey(item3Jid);

        summaryResult = submissionService.getSubmissionSummary(
                contestantHeader,
                contest.getJid(),
                Optional.of(ADMIN), // Contestant should not be able to see other users' answers
                Optional.empty()
        );

        assertThat(summaryResult.getProfile().getUsername()).isEqualTo("contestant");

        assertThat(summaryResult.getProblemAliasesMap()).containsKey(problem3.getJid());
        assertThat(summaryResult.getProblemAliasesMap().get(problem3.getJid())).isEqualTo(PROBLEM_3_ALIAS);

        assertThat(summaryResult.getProblemNamesMap()).containsKey(problem3.getJid());
        assertThat(summaryResult.getProblemNamesMap().get(problem3.getJid())).isEqualTo("Problem 3");

        assertThat(summaryResult.getConfig().getCanSupervise()).isFalse();
        assertThat(summaryResult.getConfig().getCanManage()).isFalse();

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item3Jid);

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item3Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();

        assertThat(summaryResult.getItemTypesMap()).hasSize(4);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item2Jid);
        assertThat(summaryResult.getItemTypesMap().get(item2Jid))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item3Jid);
        assertThat(summaryResult.getItemTypesMap().get(item3Jid))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item4Jid);
        assertThat(summaryResult.getItemTypesMap().get(item4Jid))
                .isEqualTo(ItemType.SHORT_ANSWER);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item5Jid);
        assertThat(summaryResult.getItemTypesMap().get(item5Jid))
                .isEqualTo(ItemType.ESSAY);

        assertThat(summaryResult.getItemJidsByProblemJid()).hasSize(1);
        assertThat(summaryResult.getItemJidsByProblemJid()).isEqualTo(
                ImmutableMap.of(
                    problem3.getJid(), ImmutableList.of(
                                item2Jid,
                                item3Jid,
                                item4Jid,
                                item5Jid
                        )
                )
        );

        summaryResult = submissionService.getSubmissionSummary(
                managerHeader,
                contest.getJid(),
                Optional.of(CONTESTANT),
                Optional.empty()
        );

        assertThat(summaryResult.getProfile().getUsername()).isEqualTo("contestant");

        assertThat(summaryResult.getProblemAliasesMap()).containsKey(problem3.getJid());
        assertThat(summaryResult.getProblemAliasesMap().get(problem3.getJid())).isEqualTo(PROBLEM_3_ALIAS);

        assertThat(summaryResult.getProblemNamesMap()).containsKey(problem3.getJid());
        assertThat(summaryResult.getProblemNamesMap().get(problem3.getJid())).isEqualTo("Problem 3");

        assertThat(summaryResult.getConfig().getCanSupervise()).isTrue();
        assertThat(summaryResult.getConfig().getCanManage()).isTrue();

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(1);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item3Jid);

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item3Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("b");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(-1).build());

        submissionService.createItemSubmission(contestantHeader, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item3Jid)
                .answer("a")
                .build()
        );

        submissionService.createItemSubmission(contestantHeader, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item2Jid)
                .answer("a")
                .build()
        );

        answersMap = submissionService.getLatestSubmissions(
                contestantHeader,
                contest.getJid(),
                Optional.empty(),
                PROBLEM_3_ALIAS
        );

        assertThat(answersMap).hasSize(2);
        assertThat(answersMap).containsKey(item3Jid);
        assertThat(answersMap).containsKey(item2Jid);

        itemSubmissionResult = answersMap.get(item3Jid);
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(problem3.getJid());
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        itemSubmissionResult = answersMap.get(item2Jid);
        assertThat(itemSubmissionResult.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(itemSubmissionResult.getProblemJid()).isEqualTo(problem3.getJid());
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item2Jid);
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading()).isEmpty();
        assertThat(itemSubmissionResult.getTime()).isAfter(Instant.EPOCH);

        summaryResult = submissionService.getSubmissionSummary(
                managerHeader,
                contest.getJid(),
                Optional.of(CONTESTANT),
                Optional.empty()
        );

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(2);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item2Jid);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item3Jid);

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item2Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item2Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item3Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4).build());

        submissionService.createItemSubmission(contestantHeader, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item5Jid)
                .answer("print('hello world!')")
                .build()
        );

        submissionService.createItemSubmission(contestantHeader, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item4Jid)
                .answer("123")
                .build()
        );

        summaryResult = submissionService.getSubmissionSummary(
                managerHeader,
                contest.getJid(),
                Optional.of(CONTESTANT),
                Optional.empty()
        );

        assertThat(summaryResult.getItemTypesMap()).hasSize(4);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item2Jid);
        assertThat(summaryResult.getItemTypesMap().get(item2Jid))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item3Jid);
        assertThat(summaryResult.getItemTypesMap().get(item3Jid))
                .isEqualTo(ItemType.MULTIPLE_CHOICE);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item4Jid);
        assertThat(summaryResult.getItemTypesMap().get(item4Jid))
                .isEqualTo(ItemType.SHORT_ANSWER);
        assertThat(summaryResult.getItemTypesMap()).containsKey(item5Jid);
        assertThat(summaryResult.getItemTypesMap().get(item5Jid))
                .isEqualTo(ItemType.ESSAY);

        assertThat(summaryResult.getSubmissionsByItemJid()).hasSize(4);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item2Jid);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item3Jid);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item4Jid);
        assertThat(summaryResult.getSubmissionsByItemJid()).containsKey(item5Jid);

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item2Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item2Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.WRONG_ANSWER).score(0.0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item3Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item3Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("a");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4.0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item4Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item4Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("123");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.ACCEPTED).score(4.0).build());

        itemSubmissionResult = summaryResult.getSubmissionsByItemJid().get(item5Jid);
        assertThat(itemSubmissionResult.getItemJid()).isEqualTo(item5Jid);
        assertThat(itemSubmissionResult.getJid()).isNotEmpty();
        assertThat(itemSubmissionResult.getAnswer()).isEqualTo("print('hello world!')");
        assertThat(itemSubmissionResult.getGrading().get()).isEqualTo(
                new Grading.Builder().verdict(Verdict.PENDING_MANUAL_GRADING).build());
    }
}
