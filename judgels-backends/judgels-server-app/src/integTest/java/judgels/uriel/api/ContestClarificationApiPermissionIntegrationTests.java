package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;

import java.time.Duration;
import java.time.Instant;
import judgels.uriel.ContestClarificationClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationAnswerData;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestClarificationClient clarificationClient = createClient(ContestClarificationClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.CLARIFICATION)
                .supervisors(SUPERVISOR_B)
                .build();
    }

    @Test
    void create_clarification() {
        contestClient.updateContest(adminToken, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(3)))
                .duration(Duration.ofHours(5))
                .build());

        assertForbidden(createClarification(adminToken));
        assertForbidden(createClarification(managerToken));
        assertForbidden(createClarification(supervisorAToken));
        assertForbidden(createClarification(supervisorBToken));
        assertForbidden(createClarification(contestantToken));
        assertForbidden(createClarification(userToken));

        enableModule(contest, CLARIFICATION);

        assertForbidden(createClarification(adminToken));
        assertForbidden(createClarification(managerToken));
        assertForbidden(createClarification(supervisorAToken));
        assertForbidden(createClarification(supervisorBToken));
        assertPermitted(createClarification(contestantToken));
        assertForbidden(createClarification(userToken));

        enableModule(contest, PAUSE);
        assertForbidden(createClarification(contestantToken));
        disableModule(contest, PAUSE);
        assertPermitted(createClarification(contestantToken));

        enableModule(contest, CLARIFICATION_TIME_LIMIT, new ContestModulesConfig.Builder()
                .clarificationTimeLimit(new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(2))
                        .build())
                .build());
        assertForbidden(createClarification(contestantToken));
        disableModule(contest, CLARIFICATION_TIME_LIMIT);
        assertPermitted(createClarification(contestantToken));

        endContest(contest);
        assertForbidden(createClarification(contestantToken));
    }

    @Test
    void answer_clarification() {
        beginContest(contest);
        enableModule(contest, CLARIFICATION);

        assertPermitted(answerClarification(adminToken));
        assertPermitted(answerClarification(managerToken));
        assertPermitted(answerClarification(supervisorAToken));
        assertForbidden(answerClarification(supervisorBToken));
        assertForbidden(answerClarification(contestantToken));
        assertForbidden(answerClarification(userToken));
    }

    @Test
    void get_clarifications() {
        beginContest(contest);

        assertForbidden(getClarifications(adminToken));
        assertForbidden(getClarifications(managerToken));
        assertForbidden(getClarifications(supervisorAToken));
        assertForbidden(getClarifications(supervisorBToken));
        assertForbidden(getClarifications(contestantToken));
        assertForbidden(getClarifications(userToken));

        enableModule(contest, CLARIFICATION);

        assertPermitted(getClarifications(adminToken));
        assertPermitted(getClarifications(managerToken));
        assertPermitted(getClarifications(supervisorAToken));
        assertPermitted(getClarifications(supervisorBToken));
        assertPermitted(getClarifications(contestantToken));
        assertForbidden(getClarifications(userToken));

        enableModule(contest, PAUSE);

        assertPermitted(getClarifications(adminToken));
        assertPermitted(getClarifications(managerToken));
        assertPermitted(getClarifications(supervisorAToken));
        assertPermitted(getClarifications(supervisorBToken));
        assertForbidden(getClarifications(contestantToken));
    }

    private ThrowingCallable createClarification(String token) {
        ContestClarificationData data = new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title(randomString())
                .question(randomString())
                .build();
        return () -> clarificationClient.createClarification(token, contest.getJid(), data);
    }

    private ThrowingCallable answerClarification(String token) {
        return () -> {
            ContestClarificationData data = new ContestClarificationData.Builder()
                    .topicJid(contest.getJid())
                    .title(randomString())
                    .question(randomString())
                    .build();
            ContestClarification clarification = clarificationClient.createClarification(contestantToken, contest.getJid(), data);
            ContestClarificationAnswerData answerData = new ContestClarificationAnswerData.Builder()
                    .answer(randomString())
                    .build();
            clarificationClient.answerClarification(token, contest.getJid(), clarification.getJid(), answerData);
        };
    }

    private ThrowingCallable getClarifications(String token) {
        return () -> clarificationClient.getClarifications(token, contest.getJid(), null);
    }
}
