package judgels.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.api.catalog.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import feign.form.FormData;
import java.util.List;
import java.util.Map;
import judgels.api.submission.programming.Submission;
import judgels.api.submission.programming.SubmissionWithSourceResponse;
import judgels.api.training.archive.ArchiveCreateData;
import judgels.api.training.problemset.ProblemSet;
import judgels.api.training.problemset.ProblemSetCreateData;
import judgels.api.training.problemset.problem.ProblemSetProblemData;
import judgels.training.submission.TrainingSubmissionClient;
import org.junit.jupiter.api.Test;

class TrainingSubmissionApiIntegrationTests extends BaseTrainingApiIntegrationTests {
    private final TrainingSubmissionClient submissionClient = createClient(TrainingSubmissionClient.class);

    @Test
    void end_to_end_flow() {
        updateProblemStatement(adminToken, problem1, "Problem 1", "text");

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
                new ProblemSetProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(PROGRAMMING).build(),
                new ProblemSetProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(PROGRAMMING).build()));

        Submission submissionA = submit(userAToken, problemSet.getJid(), problem1.getJid());
        Submission submissionB = submit(userBToken, problemSet.getJid(), problem2.getJid());

        assertThat(submissionA.getUserJid()).isEqualTo(userA.getJid());
        assertThat(submissionA.getContainerJid()).isEqualTo(problemSet.getJid());
        assertThat(submissionA.getGradingLanguage()).isEqualTo("Cpp11");

        // get submissions

        var params = new TrainingSubmissionClient.GetSubmissionsParams();
        params.containerJid = problemSet.getJid();

        var response = submissionClient.getSubmissions(userAToken, params);
        assertThat(response.getData().getPage())
                .extracting(Submission::getJid)
                .containsExactly(submissionB.getJid(), submissionA.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(userA.getJid(), userB.getJid());
        assertThat(response.getProblemAliasesMap()).isEqualTo(Map.of(
                problemSet.getJid() + "-" + problem1.getJid(), "A",
                problemSet.getJid() + "-" + problem2.getJid(), "B"));
        assertThat(response.getConfig().getCanManage()).isFalse();

        assertThat(submissionClient.getSubmissions(adminToken, params).getConfig().getCanManage()).isTrue();

        params.username = USER_A;
        assertThat(submissionClient.getSubmissions(userAToken, params).getData().getPage())
                .extracting(Submission::getJid)
                .containsExactly(submissionA.getJid());

        params = new TrainingSubmissionClient.GetSubmissionsParams();
        params.containerJid = problemSet.getJid();
        params.problemAlias = "B";
        assertThat(submissionClient.getSubmissions(userAToken, params).getData().getPage())
                .extracting(Submission::getJid)
                .containsExactly(submissionB.getJid());

        response = submissionClient.getSubmissions(userAToken, new TrainingSubmissionClient.GetSubmissionsParams());
        assertThat(response.getData().getPage())
                .extracting(Submission::getJid)
                .containsExactly(submissionB.getJid(), submissionA.getJid());
        assertThat(response.getProblemNamesMap()).containsEntry(problem1.getJid(), "Problem 1");
        assertThat(response.getContainerNamesMap()).isEqualTo(Map.of(problemSet.getJid(), "ProblemSet"));
        assertThat(response.getContainerPathsMap()).isEqualTo(Map.of(problemSet.getJid(), List.of("problemset")));

        // get submission

        assertThat(submissionClient.getSubmission(userAToken, submissionA.getJid()).getJid())
                .isEqualTo(submissionA.getJid());
        assertPermitted(() -> submissionClient.getSubmission(adminToken, submissionA.getJid()));
        assertForbidden(() -> submissionClient.getSubmission(userBToken, submissionA.getJid()));

        SubmissionWithSourceResponse sourceResponse =
                submissionClient.getSubmissionWithSourceById(userBToken, submissionA.getId());
        assertThat(sourceResponse.getData().getSubmission().getJid()).isEqualTo(submissionA.getJid());
        assertThat(sourceResponse.getData().getSource()).isPresent();
        assertThat(sourceResponse.getProfile().getUsername()).isEqualTo(USER_A);
        assertThat(sourceResponse.getProblemAlias()).isEqualTo("A");
        assertThat(sourceResponse.getProblemName()).isEqualTo("Problem 1");
        assertThat(sourceResponse.getContainerName()).isEqualTo("ProblemSet");
        assertThat(sourceResponse.getContainerPath()).containsExactly("problemset");

        // regrade

        var regradeParams = new TrainingSubmissionClient.GetSubmissionsParams();
        regradeParams.containerJid = problemSet.getJid();

        assertForbidden(() -> submissionClient.regradeSubmission(userAToken, submissionA.getJid()));
        assertForbidden(() -> submissionClient.regradeSubmissions(userAToken, regradeParams));

        assertPermitted(() -> submissionClient.regradeSubmission(adminToken, submissionA.getJid()));
        assertPermitted(() -> submissionClient.regradeSubmissions(adminToken, regradeParams));
    }

    private Submission submit(String token, String containerJid, String problemJid) {
        var file = new FormData(MULTIPART_FORM_DATA, "solution.cpp", "int main() {}".getBytes());
        return submissionClient.createSubmission(token, containerJid, problemJid, "Cpp11", file);
    }
}
