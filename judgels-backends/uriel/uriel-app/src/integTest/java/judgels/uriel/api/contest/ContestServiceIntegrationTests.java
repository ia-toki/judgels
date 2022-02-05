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
import static judgels.uriel.api.mocks.MockJophiel.USER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.role.ContestRole;
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
    }

    @Test
    void update_get_contest_description() {
        assertNotFound(() -> contestService.getContestDescription(of(ADMIN_HEADER), "bogus"));

        Contest contest = createContest();

        ContestDescription description = contestService.getContestDescription(of(ADMIN_HEADER), contest.getJid());
        assertThat(description.getDescription()).isEmpty();

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
        Contest contestA = buildContest()
                .managers(MANAGER)
                .contestants(USER_A, USER)
                .build();
        Contest contestB = buildContest()
                .managers(MANAGER)
                .supervisors(SUPERVISOR, USER)
                .contestants(USER_A, USER_B)
                .build();
        Contest contestC = buildContest()
                .modules(HIDDEN)
                .managers(MANAGER, USER)
                .supervisors(SUPERVISOR)
                .contestants(USER_A, USER_B, USER)
                .build();
        Contest contestD = buildContest()
                .modules(REGISTRATION)
                .managers(MANAGER)
                .supervisors(SUPERVISOR)
                .build();
        Contest contestE = createContest();

        Map<Optional<AuthHeader>, Set<Contest>> contestsMap = new LinkedHashMap<>();
        contestsMap.put(of(ADMIN_HEADER), ImmutableSet.of(contestA, contestB, contestC, contestD, contestE));
        contestsMap.put(of(MANAGER_HEADER), ImmutableSet.of(contestA, contestB, contestC, contestD));
        contestsMap.put(of(SUPERVISOR_HEADER), ImmutableSet.of(contestB, contestD));
        contestsMap.put(of(USER_A_HEADER), ImmutableSet.of(contestA, contestB, contestD));
        contestsMap.put(of(USER_B_HEADER), ImmutableSet.of(contestB, contestD));
        contestsMap.put(empty(), ImmutableSet.of(contestD));

        Map<Optional<AuthHeader>, Boolean> canAdministerMap = new LinkedHashMap<>();
        canAdministerMap.put(of(ADMIN_HEADER), true);
        canAdministerMap.put(of(MANAGER_HEADER), false);
        canAdministerMap.put(of(SUPERVISOR_HEADER), false);
        canAdministerMap.put(of(USER_A_HEADER), false);
        canAdministerMap.put(of(USER_B_HEADER), false);
        canAdministerMap.put(empty(), false);

        for (Optional<AuthHeader> authHeader : contestsMap.keySet()) {
            ContestsResponse response = contestService.getContests(authHeader, empty(), empty());
            assertThat(response.getData().getPage()).hasSameElementsAs(contestsMap.get(authHeader));
            assertThat(response.getConfig().getCanAdminister()).isEqualTo(canAdministerMap.get(authHeader));
        }

        ContestsResponse response = contestService.getContests(of(USER_HEADER), empty(), empty());
        assertThat(response.getRolesMap()).isEqualTo(ImmutableMap.of(
                contestA.getJid(), ContestRole.CONTESTANT,
                contestB.getJid(), ContestRole.SUPERVISOR,
                contestC.getJid(), ContestRole.MANAGER,
                contestD.getJid(), ContestRole.NONE));

        response = contestService.getContests(of(ADMIN_HEADER), empty(), empty());
        assertThat(response.getRolesMap()).isEqualTo(ImmutableMap.of(
                contestA.getJid(), ContestRole.ADMIN,
                contestB.getJid(), ContestRole.ADMIN,
                contestC.getJid(), ContestRole.ADMIN,
                contestD.getJid(), ContestRole.ADMIN,
                contestE.getJid(), ContestRole.ADMIN));
    }

    @Test
    void get_active_contests() {
        // TODO(fushar): cannot be implemented yet as unix_timestamp function is not available in H2 :(
    }
}
