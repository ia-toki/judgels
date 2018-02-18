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
                .name("TOKI Open Contest A")
                .description("This is contest A")
                .style(ContestStyle.ICPC)
                .build());

        assertThat(contestA.getName()).isEqualTo("TOKI Open Contest A");
        assertThat(contestA.getDescription()).isEqualTo("This is contest A");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);

        assertThat(contestService.getContest(contestA.getJid())).isEqualTo(contestA);

        Contest contestB = contestService.createContest(new ContestData.Builder()
                .name("TOKI Open Contest B")
                .description("This is contest B")
                .style(ContestStyle.IOI)
                .build());

        assertThat(contestService.getContest(contestB.getJid())).isEqualTo(contestB);

        contestService.createContest(new ContestData.Builder()
                .name("TOKI Open Contest - Testing")
                .description("This is testing contest")
                .style(ContestStyle.IOI)
                .build());
        contestService.createContest(new ContestData.Builder()
                .name("Random Contest")
                .description("This is random contest")
                .style(ContestStyle.IOI)
                .build());

        Page<Contest> contests = contestService.getContests(1, 10);
        assertThat(contests.getData()).containsExactly(contestA, contestB);
    }

}
