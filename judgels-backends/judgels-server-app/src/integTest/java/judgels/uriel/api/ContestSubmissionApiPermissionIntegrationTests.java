package judgels.uriel.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import feign.form.FormData;
import java.time.Duration;
import java.util.List;
import judgels.uriel.ContestSubmissionClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestSubmissionClient submissionClient = createClient(ContestSubmissionClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .modules(REGISTRATION)
                .build();

        problemClient.setProblems(managerToken, contest.getJid(), List.of(
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
        assertPermitted(submit(adminToken));
        assertPermitted(submit(managerToken));
        assertForbidden(submit(supervisorToken))
                .hasMessageContaining("You are not a contestant");
        assertForbidden(submit(contestantToken))
                .hasMessageContaining("Contest has not started yet.");
        assertForbidden(submit(userToken))
                .hasMessageContaining("You are not a contestant");

        beginContest(contest);

        assertPermitted(submit(contestantToken, problem2.getJid()));
        assertForbidden(submit(contestantToken, problem2.getJid()))
                .hasMessageContaining("Submissions limit has been reached.");
        assertForbidden(submit(contestantToken, problem3.getJid()))
                .hasMessageContaining("Problem is closed.");

        enableModule(contest, PAUSE);

        assertPermitted(submit(adminToken));
        assertPermitted(submit(managerToken));
        assertForbidden(submit(contestantToken))
                .hasMessageContaining("Contest is paused.");

        disableModule(contest, PAUSE);

        endContest(contest);

        assertForbidden(submit(adminToken))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(managerToken))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(supervisorToken));
        assertForbidden(submit(contestantToken))
                .hasMessageContaining("Contest is over.");
        assertForbidden(submit(userToken));
    }

    @Test
    void create_submission_virtual() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        assertForbidden(submit(contestantToken))
                .hasMessageContaining("Contest has not started yet.");

        beginContest(contest);
        assertForbidden(submit(contestantToken))
                .hasMessageContaining("Contest has not started yet.");

        contestClient.startVirtualContest(contestantToken, contest.getJid());
        assertPermitted(submit(contestantToken));

        // TODO(fushar): test finishing virtual contest

        endContest(contest);
        assertForbidden(submit(contestantToken))
                .hasMessageContaining("Contest is over.");
    }

    private ThrowingCallable submit(String token) {
        return submit(token, problem1.getJid());
    }

    private ThrowingCallable submit(String token, String problemJid) {
        var file = new FormData(MULTIPART_FORM_DATA, "solution.cpp", "int main() {}".getBytes());
        return () -> submissionClient.createSubmission(token, contest.getJid(), problemJid, "Cpp11", file);
    }
}
