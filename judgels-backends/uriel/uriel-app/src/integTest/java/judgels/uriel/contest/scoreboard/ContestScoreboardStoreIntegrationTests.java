package judgels.uriel.contest.scoreboard;

import static judgels.persistence.TestClock.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestScoreboardModel.class})
class ContestScoreboardStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestScoreboardStore store;
    private ContestStore contestStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestScoreboardStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        assertThat(store.getScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).isEmpty();
        assertThat(store.getScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).isEmpty();

        RawContestScoreboard scoreboard1 = store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .scoreboard("official1")
                .build());
        RawContestScoreboard scoreboard2 = store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.FROZEN)
                .scoreboard("frozen1")
                .build());

        assertThat(store.getScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).contains(scoreboard1);
        assertThat(store.getScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).contains(scoreboard2);

        // TODO(fushar): move these assertions to service integration tests instead
        assertThat(scoreboard1.getType()).isEqualTo(ContestScoreboardType.OFFICIAL);
        assertThat(scoreboard1.getScoreboard()).isEqualTo("official1");
        assertThat(scoreboard1.getUpdatedTime()).isEqualTo(NOW);

        scoreboard1 = store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .scoreboard("official2")
                .build());
        scoreboard2 = store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.FROZEN)
                .scoreboard("frozen2")
                .build());

        assertThat(store.getScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).contains(scoreboard1);
        assertThat(store.getScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).contains(scoreboard2);
    }
}
