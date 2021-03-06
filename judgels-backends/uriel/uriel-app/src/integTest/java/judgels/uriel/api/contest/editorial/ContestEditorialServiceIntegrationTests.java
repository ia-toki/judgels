package judgels.uriel.api.contest.editorial;

import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.Optional;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import org.junit.jupiter.api.Test;

public class ContestEditorialServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestEditorialService editorialService = createService(ContestEditorialService.class);
    private ContestProblemService problemService = createService(ContestProblemService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");
        contest = contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.ofEpochMilli(0))
                .build());

        ContestModulesConfig config = moduleService.getConfig(ADMIN_HEADER, contest.getJid());
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.EDITORIAL);
        moduleService.upsertConfig(ADMIN_HEADER, contest.getJid(), new ContestModulesConfig.Builder()
                .from(config)
                .editorial(new EditorialModuleConfig.Builder()
                        .preface("<p>This contest brought to you by [user:userA]</p>")
                        .build())
                .build());

        problemService.setProblems(ADMIN_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_2_SLUG)
                        .status(OPEN)
                        .build()));

        ContestEditorialResponse response = editorialService.getEditorial(contest.getJid(), Optional.empty());
        assertThat(response.getPreface()).contains("<p>This contest brought to you by [user:userA]</p>");
        assertThat(response.getProblemsMap()).containsKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getProblemEditorialsMap()).containsKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getProblemMetadatasMap()).containsKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getProfilesMap()).containsKey(USER_A_JID);
    }
}
