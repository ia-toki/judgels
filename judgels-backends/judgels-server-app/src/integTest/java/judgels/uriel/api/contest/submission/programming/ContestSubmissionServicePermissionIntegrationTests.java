package judgels.uriel.api.contest.submission.programming;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.USER_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_SLUG;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.service.api.JudgelsServiceException;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final WebTarget webTarget = createWebTarget();

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .modules(REGISTRATION)
                .build();

        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(ContestProblemStatus.OPEN)
                        .submissionsLimit(0)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_2_SLUG)
                        .status(ContestProblemStatus.OPEN)
                        .submissionsLimit(1)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("C")
                        .slug(PROBLEM_3_SLUG)
                        .status(ContestProblemStatus.CLOSED)
                        .submissionsLimit(0)
                        .build()));
    }

    @Test
    void create_submission() {
        assertPermitted(submit(ADMIN_BEARER_TOKEN));
        assertPermitted(submit(MANAGER_BEARER_TOKEN));
        assertForbidden(submit(SUPERVISOR_BEARER_TOKEN))
                .hasMessageContaining("You are not a contestant");
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN))
                .hasMessageContaining("Contest has not started yet.");
        assertForbidden(submit(USER_BEARER_TOKEN))
                .hasMessageContaining("You are not a contestant");

        beginContest(contest);

        assertPermitted(submit(CONTESTANT_BEARER_TOKEN, PROBLEM_2_JID));
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN, PROBLEM_2_JID))
                .hasMessageContaining("Submissions limit has been reached.");
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN, PROBLEM_3_JID))
                .hasMessageContaining("Problem is closed.");

        enableModule(contest, PAUSE);

        assertPermitted(submit(ADMIN_BEARER_TOKEN));
        assertPermitted(submit(MANAGER_BEARER_TOKEN));
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN))
                .hasMessageContaining("Contest is paused.");

        disableModule(contest, PAUSE);

        endContest(contest);

        assertForbidden(submit(ADMIN_BEARER_TOKEN))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(MANAGER_BEARER_TOKEN))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(SUPERVISOR_BEARER_TOKEN));
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(USER_BEARER_TOKEN));
    }

    @Test
    void create_submission_virtual() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        assertForbidden(submit(CONTESTANT_BEARER_TOKEN))
                .hasMessageContaining("Contest has not started yet.");

        beginContest(contest);
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN))
                .hasMessageContaining("Contest has not started yet.");

        contestService.startVirtualContest(CONTESTANT_HEADER, contest.getJid());
        assertPermitted(submit(CONTESTANT_BEARER_TOKEN));

        // TODO(fushar): test finishing virtual contest

        endContest(contest);
        assertForbidden(submit(CONTESTANT_BEARER_TOKEN))
                .hasMessageContaining("Contest is over.");
    }

    private ThrowingCallable submit(String token) {
        return submit(token, PROBLEM_1_JID);
    }

    private ThrowingCallable submit(String token, String problemJid) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("contestJid", contest.getJid()));
        multiPart.bodyPart(new FormDataBodyPart("problemJid", problemJid));
        multiPart.bodyPart(new FormDataBodyPart("gradingLanguage", "Cpp11"));
        multiPart.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("sourceFiles.source").fileName("solution.cpp").build(),
                "int main() {}".getBytes(),
                APPLICATION_OCTET_STREAM_TYPE));

        return () -> {
            Response response = webTarget
                    .path("/api/v2/contests/submissions/programming")
                    .request()
                    .header(AUTHORIZATION, "Bearer " + token)
                    .post(Entity.entity(multiPart, multiPart.getMediaType()));

            @SuppressWarnings("unchecked")
            Map<String, Object> body = response.readEntity(Map.class);

            if (body != null) {
                throw new JudgelsServiceException(
                        Response.Status.fromStatusCode(response.getStatus()),
                        (String) body.get("message"));
            }
        };
    }
}
