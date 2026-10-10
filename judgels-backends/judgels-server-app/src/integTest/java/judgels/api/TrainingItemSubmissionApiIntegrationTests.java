package judgels.api;

import static judgels.api.catalog.problem.ProblemType.BUNDLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import judgels.api.catalog.problem.bundle.ItemType;
import judgels.api.catalog.problem.bundle.MultipleChoiceItemConfig;
import judgels.api.catalog.problem.bundle.ShortAnswerItemConfig;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.api.submission.bundle.ItemSubmissionData;
import judgels.api.submission.bundle.Verdict;
import judgels.api.training.archive.ArchiveCreateData;
import judgels.api.training.problemset.ProblemSet;
import judgels.api.training.problemset.ProblemSetCreateData;
import judgels.api.training.problemset.problem.ProblemSetProblemData;
import judgels.client.TrainingItemSubmissionClient;
import org.junit.jupiter.api.Test;

class TrainingItemSubmissionApiIntegrationTests extends BaseTrainingApiIntegrationTests {
    private final TrainingItemSubmissionClient submissionClient = createClient(TrainingItemSubmissionClient.class);

    @Test
    void end_to_end_flow() {
        updateProblemStatement(adminToken, problem3, "Problem 3", "text");

        String item1Jid = createBundleProblemItem(adminToken, problem3, ItemType.MULTIPLE_CHOICE, "1", new MultipleChoiceItemConfig.Builder()
                .statement("<p>QUESTION 1</p>")
                .score(4)
                .penalty(-1)
                .addChoices(
                        new MultipleChoiceItemConfig.Choice.Builder()
                                .alias("a")
                                .content("answer a")
                                .isCorrect(true)
                                .build(),
                        new MultipleChoiceItemConfig.Choice.Builder()
                                .alias("b")
                                .content("answer b")
                                .isCorrect(false)
                                .build())
                .build());

        String item2Jid = createBundleProblemItem(adminToken, problem3, ItemType.SHORT_ANSWER, "2", new ShortAnswerItemConfig.Builder()
                .statement("<p>QUESTION 2</p>")
                .score(4)
                .penalty(-1)
                .inputValidationRegex("\\d+")
                .gradingRegex("123")
                .build());

        archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive")
                .name("Archive")
                .category("Category")
                .build());

        ProblemSet problemSet = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problemset")
                .name("ProblemSet")
                .archiveSlug("archive")
                .build());

        problemSetProblemClient.setProblems(adminToken, problemSet.getJid(), List.of(
                new ProblemSetProblemData.Builder().alias("C").slug(PROBLEM_3_SLUG).type(BUNDLE).build()));

        submissionClient.createItemSubmission(userAToken, new ItemSubmissionData.Builder()
                .containerJid(problemSet.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item1Jid)
                .answer("a")
                .build());

        // get submissions

        var response = submissionClient.getItemSubmissions(userAToken, problemSet.getJid(), null);
        assertThat(response.getData().getPage()).hasSize(1);
        assertThat(response.getProfilesMap()).containsOnlyKeys(userA.getJid());
        assertThat(response.getProblemAliasesMap()).isEqualTo(Map.of(problemSet.getJid() + "-" + problem3.getJid(), "C"));
        assertThat(response.getItemNumbersMap()).isEqualTo(Map.of(item1Jid, 1));
        assertThat(response.getItemTypesMap()).isEqualTo(Map.of(item1Jid, ItemType.MULTIPLE_CHOICE));
        assertThat(response.getConfig().getCanManage()).isFalse();

        ItemSubmission submission = response.getData().getPage().get(0);
        assertThat(submission.getUserJid()).isEqualTo(userA.getJid());
        assertThat(submission.getItemJid()).isEqualTo(item1Jid);
        assertThat(submission.getAnswer()).isEqualTo("a");
        assertThat(submission.getGrading().get().getVerdict()).isEqualTo(Verdict.ACCEPTED);

        var params = new TrainingItemSubmissionClient.GetItemSubmissionsParams();
        params.username = USER_B;
        assertThat(submissionClient.getItemSubmissions(adminToken, problemSet.getJid(), params).getData().getPage())
                .isEmpty();
        assertThat(submissionClient.getItemSubmissions(adminToken, problemSet.getJid(), null).getConfig().getCanManage())
                .isTrue();

        // get latest submissions

        Map<String, ItemSubmission> answersMap =
                submissionClient.getLatestItemSubmissions(userAToken, problemSet.getJid(), "C", null);
        assertThat(answersMap).containsOnlyKeys(item1Jid);
        assertThat(answersMap.get(item1Jid).getAnswer()).isEqualTo("a");
        assertThat(answersMap.get(item1Jid).getGrading()).isEmpty();

        var answersParams = new TrainingItemSubmissionClient.GetLatestItemSubmissionsParams();
        answersParams.username = USER_A;
        assertThat(submissionClient.getLatestItemSubmissions(userBToken, problemSet.getJid(), "C", answersParams))
                .isEmpty();
        assertThat(submissionClient.getLatestItemSubmissions(adminToken, problemSet.getJid(), "C", answersParams))
                .containsOnlyKeys(item1Jid);

        // get submission summary

        var summaryParams = new TrainingItemSubmissionClient.GetItemSubmissionSummaryParams();
        summaryParams.problemAlias = "C";
        var summary = submissionClient.getItemSubmissionSummary(userAToken, problemSet.getJid(), summaryParams);
        assertThat(summary.getProfile().getUsername()).isEqualTo(USER_A);
        assertThat(summary.getConfig().getCanManage()).isFalse();
        assertThat(summary.getProblemAliasesMap()).isEqualTo(Map.of(problemSet.getJid() + "-" + problem3.getJid(), "C"));
        assertThat(summary.getProblemNamesMap()).isEqualTo(Map.of(problem3.getJid(), "Problem 3"));
        assertThat(summary.getItemJidsByProblemJid()).isEqualTo(Map.of(problem3.getJid(), List.of(item1Jid, item2Jid)));
        assertThat(summary.getItemTypesMap())
                .isEqualTo(Map.of(item1Jid, ItemType.MULTIPLE_CHOICE, item2Jid, ItemType.SHORT_ANSWER));
        assertThat(summary.getSubmissionsByItemJid()).containsOnlyKeys(item1Jid);

        summaryParams.username = USER_A;
        summary = submissionClient.getItemSubmissionSummary(adminToken, problemSet.getJid(), summaryParams);
        assertThat(summary.getProfile().getUsername()).isEqualTo(USER_A);
        assertThat(summary.getConfig().getCanManage()).isTrue();

        // regrade

        var regradeParams = new TrainingItemSubmissionClient.RegradeItemSubmissionsParams();
        regradeParams.containerJid = problemSet.getJid();

        assertForbidden(() -> submissionClient.regradeItemSubmission(userAToken, submission.getJid()));
        assertForbidden(() -> submissionClient.regradeItemSubmissions(userAToken, regradeParams));

        assertPermitted(() -> submissionClient.regradeItemSubmission(adminToken, submission.getJid()));
        assertPermitted(() -> submissionClient.regradeItemSubmissions(adminToken, regradeParams));

        // clear answer

        submissionClient.createItemSubmission(userAToken, new ItemSubmissionData.Builder()
                .containerJid(problemSet.getJid())
                .problemJid(problem3.getJid())
                .itemJid(item1Jid)
                .answer("")
                .build());

        assertThat(submissionClient.getLatestItemSubmissions(userAToken, problemSet.getJid(), "C", null)).isEmpty();
    }
}
