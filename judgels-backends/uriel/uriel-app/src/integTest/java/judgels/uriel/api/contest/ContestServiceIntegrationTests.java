package judgels.uriel.api.contest;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.ContestErrors.SLUG_ALREADY_EXISTS;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.junit.jupiter.api.Test;

class ContestServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestService contestService = createService(ContestService.class);

    @Test
    void create_update_get_contest() {
        assertNotFound(() -> contestService.getContest(of(ADMIN_HEADER), "bogus"));
        assertNotFound(() -> contestService.getContestBySlug(of(ADMIN_HEADER), "bogus"));
        assertNotFound(() -> contestService.getContestDescription(of(ADMIN_HEADER), "bogus"));

        Contest contest = contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder()
                .slug("contest")
                .build());

        assertThat(contest.getSlug()).isEqualTo("contest");
        assertThat(contest.getName()).isEqualTo("contest");
        assertThat(contest.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contest.getBeginTime()).isAfter(Instant.now());
        assertThat(contestService.getContest(of(ADMIN_HEADER), contest.getJid())).isEqualTo(contest);
        assertThat(contestService.getContestBySlug(of(ADMIN_HEADER), contest.getSlug())).isEqualTo(contest);

        ContestDescription description = contestService.getContestDescription(of(ADMIN_HEADER), contest.getJid());
        assertThat(description.getDescription()).isEmpty();

        contest = contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .name("Judgels Open Contest")
                .slug("contest-new")
                .style(ContestStyle.IOI)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());

        assertThat(contest.getSlug()).isEqualTo("contest-new");
        assertThat(contest.getName()).isEqualTo("Judgels Open Contest");
        assertThat(contest.getStyle()).isEqualTo(ContestStyle.IOI);
        assertThat(contest.getBeginTime()).isEqualTo(Instant.ofEpochSecond(42));
        assertThat(contest.getDuration()).isEqualTo(Duration.ofHours(5));
        assertThat(contestService.getContest(of(ADMIN_HEADER), contest.getJid())).isEqualTo(contest);

        description = contestService.updateContestDescription(
                ADMIN_HEADER,
                contest.getJid(),
                new ContestDescription.Builder()
                        .description("This is open contest")
                        .build());
        assertThat(description.getDescription()).contains("This is open contest");
        assertThat(contestService.getContestDescription(of(ADMIN_HEADER), contest.getJid())).isEqualTo(description);
    }

    @Test
    void create_contest__bad_request() {
        createContest("contest");
        assertBadRequest(() -> contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder()
                .slug("contest")
                .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);
    }

    @Test
    void update_contest__bad_request() {
        createContest("contest-a");
        Contest contestB = createContest("contest-b");
        assertBadRequest(() ->
                contestService.updateContest(ADMIN_HEADER, contestB.getJid(), new ContestUpdateData.Builder()
                        .slug("contest-a")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);
    }

    @Test
    void get_contests() {
        Contest contestA = createContest();
        Contest contestB = createContest();
        Contest contestC = createContest();
        createContest();

        enableModule(contestC, HIDDEN);

        // as admin

        ContestsResponse response = contestService.getContests(of(ADMIN_HEADER), empty(), empty());
        assertThat(response.getData().getPage().size()).isGreaterThan(3);
        assertThat(response.getData().getPage()).contains(contestA, contestB, contestC);
        assertThat(response.getConfig().getCanAdminister()).isTrue();

        // as manager

        managerService.upsertManagers(ADMIN_HEADER, contestA.getJid(), ImmutableSet.of(MANAGER));
        managerService.upsertManagers(ADMIN_HEADER, contestB.getJid(), ImmutableSet.of(MANAGER));
        managerService.upsertManagers(ADMIN_HEADER, contestC.getJid(), ImmutableSet.of(MANAGER));

        response = contestService.getContests(of(MANAGER_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestC, contestB, contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as supervisor

        supervisorService.upsertSupervisors(ADMIN_HEADER, contestB.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(SUPERVISOR)
                .build());
        supervisorService.upsertSupervisors(ADMIN_HEADER, contestC.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(SUPERVISOR)
                .build());

        response = contestService.getContests(of(SUPERVISOR_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as contestant

        contestantService.upsertContestants(ADMIN_HEADER, contestA.getJid(), ImmutableSet.of(USER_A));
        contestantService.upsertContestants(ADMIN_HEADER, contestB.getJid(), ImmutableSet.of(USER_A, USER_B));
        contestantService.upsertContestants(ADMIN_HEADER, contestC.getJid(), ImmutableSet.of(USER_A, USER_B));

        response = contestService.getContests(of(USER_A_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB, contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        response = contestService.getContests(of(USER_B_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestB);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as user

        response = contestService.getContests(of(USER_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).isEmpty();
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as guest

        response = contestService.getContests(empty(), empty(), empty());
        assertThat(response.getData().getPage()).isEmpty();
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        enableModule(contestA, REGISTRATION);

        // as user

        response = contestService.getContests(of(USER_HEADER), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();

        // as guest

        response = contestService.getContests(empty(), empty(), empty());
        assertThat(response.getData().getPage()).containsOnly(contestA);
        assertThat(response.getConfig().getCanAdminister()).isFalse();
    }

    @Test
    void get_active_contests() {
        // TODO(fushar): cannot be implemented yet as unix_timestamp function is not available in H2 :(
    }
}
