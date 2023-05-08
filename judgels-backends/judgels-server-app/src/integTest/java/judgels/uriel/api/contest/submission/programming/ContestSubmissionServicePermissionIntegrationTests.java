package judgels.uriel.api.contest.submission.programming;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.service.api.JudgelsServiceException;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
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

class ContestSubmissionServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final WebTarget webTarget = createWebTarget();

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .modules(REGISTRATION)
                .build();

        problemService.setProblems(managerHeader, contest.getJid(), ImmutableList.of(
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
        assertPermitted(submit(adminHeader.getBearerToken()));
        assertPermitted(submit(managerHeader.getBearerToken()));
        assertForbidden(submit(supervisorHeader.getBearerToken()))
                .hasMessageContaining("You are not a contestant");
        assertForbidden(submit(contestantHeader.getBearerToken()))
                .hasMessageContaining("Contest has not started yet.");
        assertForbidden(submit(userHeader.getBearerToken()))
                .hasMessageContaining("You are not a contestant");

        beginContest(contest);

        assertPermitted(submit(contestantHeader.getBearerToken(), problem2.getJid()));
        assertForbidden(submit(contestantHeader.getBearerToken(), problem2.getJid()))
                .hasMessageContaining("Submissions limit has been reached.");
        assertForbidden(submit(contestantHeader.getBearerToken(), problem3.getJid()))
                .hasMessageContaining("Problem is closed.");

        enableModule(contest, PAUSE);

        assertPermitted(submit(adminHeader.getBearerToken()));
        assertPermitted(submit(managerHeader.getBearerToken()));
        assertForbidden(submit(contestantHeader.getBearerToken()))
                .hasMessageContaining("Contest is paused.");

        disableModule(contest, PAUSE);

        endContest(contest);

        assertForbidden(submit(adminHeader.getBearerToken()))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(managerHeader.getBearerToken()))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(supervisorHeader.getBearerToken()));
        assertForbidden(submit(contestantHeader.getBearerToken()))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(userHeader.getBearerToken()));
    }

    @Test
    void create_submission_virtual() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        assertForbidden(submit(contestantHeader.getBearerToken()))
                .hasMessageContaining("Contest has not started yet.");

        beginContest(contest);
        assertForbidden(submit(contestantHeader.getBearerToken()))
                .hasMessageContaining("Contest has not started yet.");

        contestService.startVirtualContest(contestantHeader, contest.getJid());
        assertPermitted(submit(contestantHeader.getBearerToken()));

        // TODO(fushar): test finishing virtual contest

        endContest(contest);
        assertForbidden(submit(contestantHeader.getBearerToken()))
                .hasMessageContaining("Contest is over.");
    }

    private ThrowingCallable submit(String token) {
        return submit(token, problem1.getJid());
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
