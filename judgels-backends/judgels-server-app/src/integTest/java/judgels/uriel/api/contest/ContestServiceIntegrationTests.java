package judgels.uriel.api.contest;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.ContestErrors.SLUG_ALREADY_EXISTS;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.role.ContestRole;
import org.junit.jupiter.api.Test;

class ContestServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final ContestService contestService = createService(ContestService.class);

    @Test
    void create_update_get_contest() {
        assertNotFound(() -> contestService.getContest(of(adminHeader), "bogus"));
        assertNotFound(() -> contestService.getContestBySlug(of(adminHeader), "bogus"));
        assertNotFound(() -> contestService.getContestDescription(of(adminHeader), "bogus"));

        Contest contest = contestService.createContest(adminHeader, new ContestCreateData.Builder()
                .slug("contest")
                .build());

        assertThat(contest.getSlug()).isEqualTo("contest");
        assertThat(contest.getName()).isEqualTo("contest");
        assertThat(contest.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contest.getBeginTime()).isAfter(Instant.now());
        assertThat(contestService.getContest(of(adminHeader), contest.getJid())).isEqualTo(contest);
        assertThat(contestService.getContestBySlug(of(adminHeader), contest.getSlug())).isEqualTo(contest);

        contest = contestService.updateContest(adminHeader, contest.getJid(), new ContestUpdateData.Builder()
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
        assertThat(contestService.getContest(of(adminHeader), contest.getJid())).isEqualTo(contest);
    }

    @Test
    void update_get_contest_description() {
        assertNotFound(() -> contestService.getContestDescription(of(adminHeader), "bogus"));

        Contest contest = createContest();

        ContestDescription description = contestService.getContestDescription(of(adminHeader), contest.getJid());
        assertThat(description.getDescription()).isEmpty();

        description = contestService.updateContestDescription(
                adminHeader,
                contest.getJid(),
                new ContestDescription.Builder()
                        .description("This is open contest")
                        .build());

        assertThat(description.getDescription()).contains("This is open contest");
        assertThat(contestService.getContestDescription(of(adminHeader), contest.getJid())).isEqualTo(description);
    }

    @Test
    void create_contest__bad_request() {
        createContest("contest");
        assertBadRequest(() -> contestService.createContest(adminHeader, new ContestCreateData.Builder()
                .slug("contest")
                .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);
    }

    @Test
    void update_contest__bad_request() {
        createContest("contest-a");
        Contest contestB = createContest("contest-b");
        assertBadRequest(() ->
                contestService.updateContest(adminHeader, contestB.getJid(), new ContestUpdateData.Builder()
                        .slug("contest-a")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);
    }

    @Test
    void get_contests_and_active_contests() {
        // contests will start in the future, considered as active

        Contest contestA = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(10)))
                .managers(MANAGER)
                .contestants(CONTESTANT_A, USER)
                .build();
        Contest contestB = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(9)))
                .managers(MANAGER)
                .supervisors(SUPERVISOR, USER)
                .contestants(CONTESTANT_A, CONTESTANT_B)
                .build();
        Contest contestC = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(8)))
                .modules(HIDDEN)
                .managers(MANAGER, USER)
                .supervisors(SUPERVISOR)
                .contestants(CONTESTANT_A, CONTESTANT_B, USER)
                .build();
        Contest contestD = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(7)))
                .modules(REGISTRATION)
                .managers(MANAGER)
                .supervisors(SUPERVISOR)
                .build();
        Contest contestE = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(6)))
                .build();

        Map<Optional<AuthHeader>, List<Contest>> contestsMap = new LinkedHashMap<>();
        contestsMap.put(of(adminHeader), ImmutableList.of(contestA, contestB, contestC, contestD, contestE));
        contestsMap.put(of(managerHeader), ImmutableList.of(contestA, contestB, contestC, contestD));
        contestsMap.put(of(supervisorHeader), ImmutableList.of(contestB, contestD));
        contestsMap.put(of(contestantAHeader), ImmutableList.of(contestA, contestB, contestD));
        contestsMap.put(of(contestantBHeader), ImmutableList.of(contestB, contestD));
        contestsMap.put(empty(), ImmutableList.of(contestD));

        for (Optional<AuthHeader> authHeader : contestsMap.keySet()) {
            ContestsResponse response = contestService.getContests(authHeader, empty(), empty());
            assertThat(response.getData().getPage()).containsExactlyElementsOf(contestsMap.get(authHeader));
            assertThat(response.getConfig().getCanAdminister()).isEqualTo(authHeader.equals(of(adminHeader)));

            ActiveContestsResponse activeResponse = contestService.getActiveContests(authHeader);
            assertThat(activeResponse.getData()).containsExactlyElementsOf(Lists.reverse(contestsMap.get(authHeader)));
        }

        ContestsResponse response = contestService.getContests(of(userHeader), empty(), empty());
        assertThat(response.getRolesMap()).isEqualTo(ImmutableMap.of(
                contestA.getJid(), ContestRole.CONTESTANT,
                contestB.getJid(), ContestRole.SUPERVISOR,
                contestC.getJid(), ContestRole.MANAGER,
                contestD.getJid(), ContestRole.NONE));

        ActiveContestsResponse activeResponse = contestService.getActiveContests(of(userHeader));
        assertThat(activeResponse.getRolesMap()).isEqualTo(response.getRolesMap());

        response = contestService.getContests(of(adminHeader), empty(), empty());
        assertThat(response.getRolesMap()).isEqualTo(ImmutableMap.of(
                contestA.getJid(), ContestRole.ADMIN,
                contestB.getJid(), ContestRole.ADMIN,
                contestC.getJid(), ContestRole.ADMIN,
                contestD.getJid(), ContestRole.ADMIN,
                contestE.getJid(), ContestRole.ADMIN));

        activeResponse = contestService.getActiveContests(of(adminHeader));
        assertThat(activeResponse.getRolesMap()).isEqualTo(response.getRolesMap());

        // contests are now running, considered as active

        contestA = beginContest(contestA);
        contestB = beginContest(contestB);
        contestC = beginContest(contestC);
        contestD = beginContest(contestD);
        contestE = beginContest(contestE);

        contestsMap.put(of(adminHeader), ImmutableList.of(contestA, contestB, contestC, contestD, contestE));
        contestsMap.put(of(managerHeader), ImmutableList.of(contestA, contestB, contestC, contestD));
        contestsMap.put(of(supervisorHeader), ImmutableList.of(contestB, contestD));
        contestsMap.put(of(contestantAHeader), ImmutableList.of(contestA, contestB, contestD));
        contestsMap.put(of(contestantBHeader), ImmutableList.of(contestB, contestD));
        contestsMap.put(empty(), ImmutableList.of(contestD));

        for (Optional<AuthHeader> authHeader : contestsMap.keySet()) {
            activeResponse = contestService.getActiveContests(authHeader);
            assertThat(activeResponse.getData()).containsExactlyElementsOf(contestsMap.get(authHeader));
        }

        // contests have ended, considered as inactive

        endContest(contestA);
        endContest(contestB);
        endContest(contestC);
        endContest(contestD);
        endContest(contestE);

        for (Optional<AuthHeader> authHeader : contestsMap.keySet()) {
            activeResponse = contestService.getActiveContests(authHeader);
            assertThat(activeResponse.getData()).isEmpty();
        }
    }
}
