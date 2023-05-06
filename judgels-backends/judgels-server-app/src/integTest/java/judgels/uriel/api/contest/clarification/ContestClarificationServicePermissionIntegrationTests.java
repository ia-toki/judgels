package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;

import java.time.Duration;
import java.time.Instant;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
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
        contestService.updateContest(adminHeader, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(3)))
                .duration(Duration.ofHours(5))
                .build());

        assertForbidden(createClarification(adminHeader));
        assertForbidden(createClarification(managerHeader));
        assertForbidden(createClarification(supervisorAHeader));
        assertForbidden(createClarification(supervisorBHeader));
        assertForbidden(createClarification(contestantHeader));
        assertForbidden(createClarification(userHeader));

        enableModule(contest, CLARIFICATION);

        assertForbidden(createClarification(adminHeader));
        assertForbidden(createClarification(managerHeader));
        assertForbidden(createClarification(supervisorAHeader));
        assertForbidden(createClarification(supervisorBHeader));
        assertPermitted(createClarification(contestantHeader));
        assertForbidden(createClarification(userHeader));

        enableModule(contest, PAUSE);
        assertForbidden(createClarification(contestantHeader));
        disableModule(contest, PAUSE);
        assertPermitted(createClarification(contestantHeader));

        enableModule(contest, CLARIFICATION_TIME_LIMIT, new ContestModulesConfig.Builder()
                .clarificationTimeLimit(new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(2))
                        .build())
                .build());
        assertForbidden(createClarification(contestantHeader));
        disableModule(contest, CLARIFICATION_TIME_LIMIT);
        assertPermitted(createClarification(contestantHeader));

        endContest(contest);
        assertForbidden(createClarification(contestantHeader));
    }

    @Test
    void answer_clarification() {
        beginContest(contest);
        enableModule(contest, CLARIFICATION);

        assertPermitted(answerClarification(adminHeader));
        assertPermitted(answerClarification(managerHeader));
        assertPermitted(answerClarification(supervisorAHeader));
        assertForbidden(answerClarification(supervisorBHeader));
        assertForbidden(answerClarification(contestantHeader));
        assertForbidden(answerClarification(userHeader));
    }

    @Test
    void get_clarifications() {
        beginContest(contest);

        assertForbidden(getClarifications(adminHeader));
        assertForbidden(getClarifications(managerHeader));
        assertForbidden(getClarifications(supervisorAHeader));
        assertForbidden(getClarifications(supervisorBHeader));
        assertForbidden(getClarifications(contestantHeader));
        assertForbidden(getClarifications(userHeader));

        enableModule(contest, CLARIFICATION);

        assertPermitted(getClarifications(adminHeader));
        assertPermitted(getClarifications(managerHeader));
        assertPermitted(getClarifications(supervisorAHeader));
        assertPermitted(getClarifications(supervisorBHeader));
        assertPermitted(getClarifications(contestantHeader));
        assertForbidden(getClarifications(userHeader));

        enableModule(contest, PAUSE);

        assertPermitted(getClarifications(adminHeader));
        assertPermitted(getClarifications(managerHeader));
        assertPermitted(getClarifications(supervisorAHeader));
        assertPermitted(getClarifications(supervisorBHeader));
        assertForbidden(getClarifications(contestantHeader));
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
                    clarificationService.createClarification(contestantHeader, contest.getJid(), data);
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
