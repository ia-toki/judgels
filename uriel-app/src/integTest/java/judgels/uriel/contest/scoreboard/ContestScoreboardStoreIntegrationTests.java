package judgels.uriel.contest.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.hibernate.ContestHibernateDao;
import judgels.uriel.hibernate.ContestScoreboardHibernateDao;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.role.RoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestScoreboardModel.class})
class ContestScoreboardStoreIntegrationTests {
    private ContestScoreboardStore store;
    private ContestStore contestStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        ContestDao contestDao = new ContestHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());
        ContestScoreboardDao scoreboardDao = new ContestScoreboardHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());
        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());
        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        RoleStore roleStore = new RoleStore(adminRoleDao, contestantDao);

        contestStore = new ContestStore(roleStore, contestDao);
        store = new ContestScoreboardStore(scoreboardDao);
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder()
                .name("contestA")
                .description("contest A")
                .style(ContestStyle.IOI)
                .build());

        contestStore.createContest(new ContestData.Builder()
                .name("contestB")
                .description("contest B")
                .style(ContestStyle.IOI)
                .build());

        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).isEmpty();
        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).isEmpty();

        store.upsertScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL, "official1");
        store.upsertScoreboard(contest.getJid(), ContestScoreboardType.FROZEN, "frozen1");

        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).contains(
                new ContestScoreboardData.Builder()
                        .type(ContestScoreboardType.OFFICIAL)
                        .scoreboard("official1")
                        .build());
        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).contains(
                new ContestScoreboardData.Builder()
                        .type(ContestScoreboardType.FROZEN)
                        .scoreboard("frozen1")
                        .build());

        store.upsertScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL, "official2");
        store.upsertScoreboard(contest.getJid(), ContestScoreboardType.FROZEN, "frozen2");

        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL)).contains(
                new ContestScoreboardData.Builder()
                        .type(ContestScoreboardType.OFFICIAL)
                        .scoreboard("official2")
                        .build());
        assertThat(store.findScoreboard(contest.getJid(), ContestScoreboardType.FROZEN)).contains(
                new ContestScoreboardData.Builder()
                        .type(ContestScoreboardType.FROZEN)
                        .scoreboard("frozen2")
                        .build());
    }
}
