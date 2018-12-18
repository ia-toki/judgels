package judgels.uriel.api.contest;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.time.Duration;
import java.time.Instant;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.module.ContestModuleType;
import org.junit.jupiter.api.Test;

class ContestServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestService contestService = createService(ContestService.class);

    @Test
    void end_to_end_flow() {
        // as admin

        Contest contestA = createContestWithRoles("contest-a");
        Contest contestB = createContestWithRoles("contest-b");

        // as manager

        contestA = contestService.updateContest(MANAGER_HEADER, contestA.getJid(), new ContestUpdateData.Builder()
                .name("Judgels Open Contest A")
                .slug("contest-a")
                .style(ContestStyle.ICPC)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());
        contestService.updateContestDescription(MANAGER_HEADER, contestA.getJid(), new ContestDescription.Builder()
                .description("This is contest A")
                .build());

        // as admin

        contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder().slug("contest-testing").build());
        contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder().slug("contest-random").build());

        ContestsResponse response = contestService.getContests(of(ADMIN_HEADER), empty(), empty());
        assertThat(response.getData().getPage().size()).isEqualTo(4);
        assertThat(response.getConfig().getCanAdminister()).isTrue();

        // as manager

        contestantService.upsertContestants(MANAGER_HEADER, contestA.getJid(), ImmutableSet.of(USER_A));
        contestantService.upsertContestants(MANAGER_HEADER, contestB.getJid(), ImmutableSet.of(USER_A, USER_B));

        response = contestService.getContests(of(MANAGER_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB, contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as contestant

        response = contestService.getContests(of(USER_A_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB, contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        response = contestService.getContests(of(USER_B_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as non-viewer

        String contestAJid = contestA.getJid();
        assertThatRemoteExceptionThrownBy(
                () -> contestService.getContest(of(AuthHeader.of("randomToken")), contestAJid))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        // as viewer

        moduleService.enableModule(MANAGER_HEADER, contestA.getJid(), ContestModuleType.REGISTRATION);
        moduleService.enableModule(MANAGER_HEADER, contestB.getJid(), ContestModuleType.REGISTRATION);

        contestA = contestService.getContest(of(USER_HEADER), contestA.getJid());
        assertThat(contestService.getContestBySlug(of(USER_HEADER), contestA.getSlug())).isEqualTo(contestA);

        assertThat(contestA.getSlug()).isEqualTo("contest-a");
        assertThat(contestA.getName()).isEqualTo("Judgels Open Contest A");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contestA.getBeginTime()).isEqualTo(Instant.ofEpochSecond(42));
        assertThat(contestA.getDuration()).isEqualTo(Duration.ofHours(5));

        ContestDescription descriptionA = contestService.getContestDescription(of(USER_HEADER), contestA.getJid());
        assertThat(descriptionA.getDescription()).isEqualTo("This is contest A");

        assertThat(contestService.getContest(of(USER_HEADER), contestB.getJid())).isEqualTo(contestB);
        assertThat(contestService.getContestBySlug(of(USER_HEADER), "" + contestB.getId())).isEqualTo(contestB);
    }
}
