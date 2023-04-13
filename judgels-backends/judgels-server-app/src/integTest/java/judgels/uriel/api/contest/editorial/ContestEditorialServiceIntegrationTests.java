package judgels.uriel.api.contest.editorial;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.EDITORIAL;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContestEditorialServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestEditorialService editorialService = createService(ContestEditorialService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContest()
                .ended()
                .problems(
                        "A", PROBLEM_1_SLUG,
                        "B", PROBLEM_2_SLUG)
                .build();
    }

    @Test
    void get_editorial() {
        enableModule(contest, EDITORIAL, new ContestModulesConfig.Builder()
                .editorial(new EditorialModuleConfig.Builder()
                        .preface("<p>This contest brought to you by [user:userA]</p>")
                        .build())
                .build());

        ContestEditorialResponse response = editorialService.getEditorial(contest.getJid(), empty());
        assertThat(response.getPreface()).contains("<p>This contest brought to you by [user:userA]</p>");
        assertThat(response.getProblemsMap()).containsOnlyKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getProblemEditorialsMap()).containsKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getProblemMetadatasMap()).containsKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getProfilesMap()).containsOnlyKeys(USER_A_JID);
    }
}
