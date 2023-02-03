package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;

import java.time.Duration;
import java.time.Instant;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestClarificationService clarificationService = createService(ContestClarificationService.class);

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
        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(3)))
                .duration(Duration.ofHours(5))
                .build());

        assertForbidden(createClarification(ADMIN_HEADER));
        assertForbidden(createClarification(MANAGER_HEADER));
        assertForbidden(createClarification(SUPERVISOR_A_HEADER));
        assertForbidden(createClarification(SUPERVISOR_B_HEADER));
        assertForbidden(createClarification(CONTESTANT_HEADER));
        assertForbidden(createClarification(USER_HEADER));

        enableModule(contest, CLARIFICATION);

        assertForbidden(createClarification(ADMIN_HEADER));
        assertForbidden(createClarification(MANAGER_HEADER));
        assertForbidden(createClarification(SUPERVISOR_A_HEADER));
        assertForbidden(createClarification(SUPERVISOR_B_HEADER));
        assertPermitted(createClarification(CONTESTANT_HEADER));
        assertForbidden(createClarification(USER_HEADER));

        enableModule(contest, PAUSE);
        assertForbidden(createClarification(CONTESTANT_HEADER));
        disableModule(contest, PAUSE);
        assertPermitted(createClarification(CONTESTANT_HEADER));

        enableModule(contest, CLARIFICATION_TIME_LIMIT, new ContestModulesConfig.Builder()
                .clarificationTimeLimit(new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(2))
                        .build())
                .build());
        assertForbidden(createClarification(CONTESTANT_HEADER));
        disableModule(contest, CLARIFICATION_TIME_LIMIT);
        assertPermitted(createClarification(CONTESTANT_HEADER));

        endContest(contest);
        assertForbidden(createClarification(CONTESTANT_HEADER));
    }

    @Test
    void answer_clarification() {
        beginContest(contest);
        enableModule(contest, CLARIFICATION);

        assertPermitted(answerClarification(ADMIN_HEADER));
        assertPermitted(answerClarification(MANAGER_HEADER));
        assertPermitted(answerClarification(SUPERVISOR_A_HEADER));
        assertForbidden(answerClarification(SUPERVISOR_B_HEADER));
        assertForbidden(answerClarification(CONTESTANT_HEADER));
        assertForbidden(answerClarification(USER_HEADER));
    }

    @Test
    void get_clarifications() {
        beginContest(contest);

        assertForbidden(getClarifications(ADMIN_HEADER));
        assertForbidden(getClarifications(MANAGER_HEADER));
        assertForbidden(getClarifications(SUPERVISOR_A_HEADER));
        assertForbidden(getClarifications(SUPERVISOR_B_HEADER));
        assertForbidden(getClarifications(CONTESTANT_HEADER));
        assertForbidden(getClarifications(USER_HEADER));

        enableModule(contest, CLARIFICATION);

        assertPermitted(getClarifications(ADMIN_HEADER));
        assertPermitted(getClarifications(MANAGER_HEADER));
        assertPermitted(getClarifications(SUPERVISOR_A_HEADER));
        assertPermitted(getClarifications(SUPERVISOR_B_HEADER));
        assertPermitted(getClarifications(CONTESTANT_HEADER));
        assertForbidden(getClarifications(USER_HEADER));

        enableModule(contest, PAUSE);

        assertPermitted(getClarifications(ADMIN_HEADER));
        assertPermitted(getClarifications(MANAGER_HEADER));
        assertPermitted(getClarifications(SUPERVISOR_A_HEADER));
        assertPermitted(getClarifications(SUPERVISOR_B_HEADER));
        assertForbidden(getClarifications(CONTESTANT_HEADER));
    }

    private ThrowingCallable createClarification(AuthHeader authHeader) {
        ContestClarificationData data = new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title(randomString())
                .question(randomString())
                .build();
        return () -> clarificationService.createClarification(authHeader, contest.getJid(), data);
    }

    private ThrowingCallable answerClarification(AuthHeader authHeader) {
        return () -> {
            ContestClarificationData data = new ContestClarificationData.Builder()
                    .topicJid(contest.getJid())
                    .title(randomString())
                    .question(randomString())
                    .build();
            ContestClarification clarification =
                    clarificationService.createClarification(CONTESTANT_HEADER, contest.getJid(), data);
            ContestClarificationAnswerData answerData = new ContestClarificationAnswerData.Builder()
                    .answer(randomString())
                    .build();
            clarificationService.answerClarification(authHeader, contest.getJid(), clarification.getJid(), answerData);
        };
    }

    private ThrowingCallable getClarifications(AuthHeader authHeader) {
        return () ->
                clarificationService.getClarifications(authHeader, contest.getJid(), empty(), empty(), empty());
    }
}
