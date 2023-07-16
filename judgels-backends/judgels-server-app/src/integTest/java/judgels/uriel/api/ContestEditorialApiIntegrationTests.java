package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.EDITORIAL;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.ContestEditorialClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContestEditorialApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestEditorialClient editorialClient = createClient(ContestEditorialClient.class);

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
        createProblemEditorial(managerToken, problem1);

        enableModule(contest, EDITORIAL, new ContestModulesConfig.Builder()
                .editorial(new EditorialModuleConfig.Builder()
                        .preface("<p>This contest brought to you by [user:userA]</p>")
                        .build())
                .build());

        var response = editorialClient.getEditorial(null, contest.getJid());
        assertThat(response.getPreface()).contains("<p>This contest brought to you by [user:userA]</p>");
        assertThat(response.getProblemsMap()).containsOnlyKeys(problem1.getJid(), problem2.getJid());
        assertThat(response.getProblemEditorialsMap()).containsKeys(problem1.getJid());
        assertThat(response.getProblemMetadatasMap()).containsKeys(problem1.getJid(), problem2.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(userA.getJid());
    }
}
