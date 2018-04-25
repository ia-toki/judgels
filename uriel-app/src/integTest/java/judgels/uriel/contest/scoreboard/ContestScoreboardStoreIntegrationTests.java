package judgels.uriel.contest.scoreboard;

import static judgels.uriel.UrielIntegrationTestPersistenceModule.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestScoreboardModel.class})
class ContestScoreboardStoreIntegrationTests {
    private ContestScoreboardStore store;
    private ContestStore contestStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        contestStore = component.contestStore();
        store = component.contestScoreboardStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder().name("contestA").build());
        contestStore.createContest(new ContestData.Builder().name("contestB").build());

        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).isEmpty();
        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).isEmpty();

        store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .scoreboard("official1")
                .build());
        store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.FROZEN)
                .scoreboard("frozen1")
                .build());

        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).contains(
                new RawContestScoreboard.Builder()
                        .type(ContestScoreboardType.OFFICIAL)
                        .scoreboard("official1")
                        .updatedTime(NOW)
                        .build());
        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).contains(
                new RawContestScoreboard.Builder()
                        .type(ContestScoreboardType.FROZEN)
                        .scoreboard("frozen1")
                        .updatedTime(NOW)
                        .build());

        store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.OFFICIAL)
                .scoreboard("official2")
                .build());
        store.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .type(ContestScoreboardType.FROZEN)
                .scoreboard("frozen2")
                .build());

        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).contains(
                new RawContestScoreboard.Builder()
                        .type(ContestScoreboardType.OFFICIAL)
                        .scoreboard("official2")
                        .updatedTime(NOW)
                        .build());
        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).contains(
                new RawContestScoreboard.Builder()
                        .type(ContestScoreboardType.FROZEN)
                        .scoreboard("frozen2")
                        .updatedTime(NOW)
                        .build());
    }
}
