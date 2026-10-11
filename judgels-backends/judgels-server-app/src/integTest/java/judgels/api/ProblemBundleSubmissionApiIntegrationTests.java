package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.PartnerPermission;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.bundle.EssayItemConfig;
import judgels.api.catalog.problem.bundle.ItemType;
import judgels.api.catalog.problem.bundle.MultipleChoiceItemConfig;
import judgels.api.catalog.problem.partner.ProblemPartnerData;
import judgels.api.submission.bundle.BundleAnswer;
import judgels.api.submission.bundle.BundleSubmission;
import judgels.api.submission.bundle.ProblemBundleSubmissionWithAnswerResponse;
import judgels.api.submission.bundle.ProblemBundleSubmissionsResponse;
import judgels.api.user.User;
import judgels.client.ProblemBundleSubmissionClient;
import judgels.client.ProblemItemClient;
import judgels.client.ProblemPartnerClient;
import judgels.client.ProblemVersionClient;
import org.junit.jupiter.api.Test;

class ProblemBundleSubmissionApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemPartnerClient partnerClient = createClient(ProblemPartnerClient.class);
    private final ProblemItemClient itemClient = createClient(ProblemItemClient.class);
    private final ProblemBundleSubmissionClient submissionClient = createClient(ProblemBundleSubmissionClient.class);
    private final ProblemVersionClient versionClient = createClient(ProblemVersionClient.class);

    @Test
    void submit_get_regrade_submissions() {
        User viewer = createUser("bundle-submission-viewer");
        String viewerToken = getToken(viewer);

        Problem problem = createBundleProblem(adminToken, "bundle-submission-problem");
        String problemJid = problem.getJid();

        partnerClient.setPartners(adminToken, problemJid, List.of(new ProblemPartnerData.Builder()
                .username(viewer.getUsername())
                .permission(PartnerPermission.VIEW)
                .build()));

        String choiceItemJid = createBundleProblemItem(adminToken, problem, ItemType.MULTIPLE_CHOICE, "1",
                new MultipleChoiceItemConfig.Builder()
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
        String essayItemJid = createBundleProblemItem(adminToken, problem, ItemType.ESSAY, "2",
                new EssayItemConfig.Builder()
                        .statement("<p>QUESTION 2</p>")
                        .score(10)
                        .build());

        // submit

        submissionClient.createBundleSubmission(adminToken, problemJid, answer(Map.of(choiceItemJid, "b")));
        submissionClient.createBundleSubmission(adminToken, problemJid, answer(Map.of(
                choiceItemJid, "a",
                essayItemJid, "")));

        assertBadRequest(() -> submissionClient.createBundleSubmission(adminToken, problemJid, new BundleAnswer.Builder()
                .answers(Map.of(choiceItemJid, "a"))
                .languageCode("id-ID")
                .build()));

        // get submissions

        ProblemBundleSubmissionsResponse response = submissionClient.getBundleSubmissions(viewerToken, problemJid);
        List<BundleSubmission> submissions = response.getData().getPage();
        assertThat(submissions).hasSize(2);
        assertThat(submissions).extracting(BundleSubmission::getAuthorJid).containsOnly(admin.getJid());
        assertThat(submissions).extracting(s -> s.getLatestGrading().getScore()).containsExactly(4.0, -1.0);
        assertThat(response.getProfilesMap()).containsOnlyKeys(admin.getJid());

        BundleSubmission submission = submissions.get(0);

        // get submission

        ProblemBundleSubmissionWithAnswerResponse answerResponse =
                submissionClient.getBundleSubmissionWithAnswerById(viewerToken, problemJid, submission.getId());
        assertThat(answerResponse.getData().getJid()).isEqualTo(submission.getJid());
        // The essay was left empty, so it was not answered.
        assertThat(answerResponse.getAnswer().getAnswers()).isEqualTo(Map.of(choiceItemJid, "a"));
        assertThat(answerResponse.getData().getLatestGrading().getDetails()).containsOnlyKeys(choiceItemJid);
        assertThat(answerResponse.getProfile().getUsername()).isEqualTo(admin.getUsername());

        // A submission is reachable only through its own problem.
        String otherProblemJid = createBundleProblem(adminToken, "bundle-submission-other-problem").getJid();
        assertNotFound(() -> submissionClient
                .getBundleSubmissionWithAnswerById(adminToken, otherProblemJid, submission.getId()));
        assertNotFound(() -> submissionClient
                .regradeBundleSubmission(adminToken, otherProblemJid, submission.getJid()));

        // regrade

        assertPermitted(() -> submissionClient.regradeBundleSubmission(adminToken, problemJid, submission.getJid()));
        assertPermitted(() -> submissionClient.regradeBundleSubmissions(adminToken, problemJid));

        assertThat(submissionClient.getBundleSubmissions(adminToken, problemJid).getData().getPage())
                .extracting(s -> s.getLatestGrading().getScore())
                .containsExactly(4.0, -1.0);

        // as viewer

        assertForbidden(() -> submissionClient
                .createBundleSubmission(viewerToken, problemJid, answer(Map.of(choiceItemJid, "a"))));
        assertForbidden(() -> submissionClient.regradeBundleSubmission(viewerToken, problemJid, submission.getJid()));
        assertForbidden(() -> submissionClient.regradeBundleSubmissions(viewerToken, problemJid));

        // as user

        assertForbidden(() -> submissionClient.getBundleSubmissions(userToken, problemJid));
        assertForbidden(() -> submissionClient
                .getBundleSubmissionWithAnswerById(userToken, problemJid, submission.getId()));

        // with local changes

        itemClient.moveItemDown(adminToken, problemJid, choiceItemJid);

        assertForbidden(() -> submissionClient
                .createBundleSubmission(adminToken, problemJid, answer(Map.of(choiceItemJid, "a"))));
        assertPermitted(() -> submissionClient.regradeBundleSubmissions(adminToken, problemJid));

        versionClient.discardVersionLocalChanges(adminToken, problemJid);

        assertPermitted(() -> submissionClient
                .createBundleSubmission(adminToken, problemJid, answer(Map.of(choiceItemJid, "a"))));
    }

    @Test
    void programming_problem() {
        String problemJid = createProblem(adminToken, "bundle-submission-programming-problem").getJid();

        assertNotFound(() -> submissionClient.getBundleSubmissions(adminToken, problemJid));
        assertNotFound(() -> submissionClient.createBundleSubmission(adminToken, problemJid, answer(Map.of())));
        assertNotFound(() -> submissionClient.regradeBundleSubmissions(adminToken, problemJid));
    }

    private static BundleAnswer answer(Map<String, String> answers) {
        return new BundleAnswer.Builder()
                .answers(answers)
                .languageCode("en-US")
                .build();
    }
}
