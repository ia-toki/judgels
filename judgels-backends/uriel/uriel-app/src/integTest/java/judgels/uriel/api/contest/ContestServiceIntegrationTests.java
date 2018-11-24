package judgels.uriel.api.contest;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.time.Duration;
import java.time.Instant;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import org.junit.jupiter.api.Test;

class ContestServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestService contestService = createService(ContestService.class);
    private ContestContestantService contestantService = createService(ContestContestantService.class);

    @Test
    void end_to_end_flow() {
        Contest contestA =
                contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder().slug("contest-a").build());
        contestA = contestService.updateContest(ADMIN_HEADER, contestA.getJid(), new ContestUpdateData.Builder()
                .name("Judgels Open Contest A")
                .slug("contest-a")
                .style(ContestStyle.ICPC)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());
        contestService.updateContestDescription(ADMIN_HEADER, contestA.getJid(), new ContestDescription.Builder()
                .description("This is contest A")
                .build());

        assertThat(contestA.getSlug()).isEqualTo("contest-a");
        assertThat(contestA.getName()).isEqualTo("Judgels Open Contest A");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contestA.getBeginTime()).isEqualTo(Instant.ofEpochSecond(42));
        assertThat(contestA.getDuration()).isEqualTo(Duration.ofHours(5));

        ContestDescription descriptionA = contestService.getContestDescription(of(ADMIN_HEADER), contestA.getJid());
        assertThat(descriptionA.getDescription()).isEqualTo("This is contest A");

        assertThat(contestService.getContest(of(ADMIN_HEADER), contestA.getJid())).isEqualTo(contestA);
        assertThat(contestService.getContestBySlug(of(ADMIN_HEADER), contestA.getSlug())).isEqualTo(contestA);

        String contestAJid = contestA.getJid();
        assertThatRemoteExceptionThrownBy(
                () -> contestService.getContest(of(AuthHeader.of("randomToken")), contestAJid))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        Contest contestB =
                contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder().slug("contest-b").build());

        assertThat(contestService.getContest(of(ADMIN_HEADER), contestB.getJid())).isEqualTo(contestB);
        assertThat(contestService.getContestBySlug(of(ADMIN_HEADER), "" + contestB.getId())).isEqualTo(contestB);

        contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder().slug("contest-testing").build());
        contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder().slug("contest-random").build());

        contestantService.upsertContestants(ADMIN_HEADER, contestA.getJid(), ImmutableSet.of(USER_A));
        contestantService.upsertContestants(ADMIN_HEADER, contestB.getJid(), ImmutableSet.of(USER_A, USER_B));

        ContestsResponse response = contestService.getContests(of(USER_A_HEADER), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB, contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        response = contestService.getContests(of(USER_B_HEADER), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        response = contestService.getContests(of(ADMIN_HEADER), empty());
        assertThat(response.getConfig().getCanAdminister()).isTrue();
    }
}
