package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.api.Page;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestStyle;
import org.junit.jupiter.api.Test;

class ContestServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private ContestService contestService = createService(ContestService.class);

    @Test void basic_flow() {
        Contest contestA = contestService.createContest(new ContestData.Builder()
                .name("cf212")
                .description("codeforces 212")
                .style(ContestStyle.ICPC)
                .build());

        assertThat(contestA.getName()).isEqualTo("cf212");
        assertThat(contestA.getDescription()).isEqualTo("codeforces 212");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);

        assertThat(contestService.getContest(contestA.getJid())).isEqualTo(contestA);

        Contest contestB = contestService.createContest(new ContestData.Builder()
                .name("acf212")
                .description("alumni cf 212")
                .style(ContestStyle.IOI)
                .build());

        assertThat(contestService.getContest(contestB.getJid())).isEqualTo(contestB);

        Page<Contest> contests = contestService.getContests(1, 10);
        assertThat(contests.getTotalItems()).isEqualTo(2);
        assertThat(contests.getData()).contains(contestA, contestB);
    }

}
