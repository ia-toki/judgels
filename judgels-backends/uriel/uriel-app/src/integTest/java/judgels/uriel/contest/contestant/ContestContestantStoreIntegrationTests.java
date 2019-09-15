package judgels.uriel.contest.contestant;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestContestantModel.class})
class ContestContestantStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestContestantStore store;
    private ContestStore contestStore;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestContestantStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest").build());

        assertThat(store.upsertContestant(contest.getJid(), "userJidA")).isTrue();
        assertThat(store.upsertContestant(contest.getJid(), "userJidB")).isTrue();
        assertThat(store.upsertContestant(contest.getJid(), "userJidA")).isFalse();

        Set<String> contestantJids = store.getApprovedContestantJids(contest.getJid());
        assertThat(contestantJids).containsOnly("userJidA", "userJidB");

        Page<ContestContestant> contestants = store.getContestants(contest.getJid(), Optional.empty());
        assertThat(contestants.getPage()).containsExactly(
                new ContestContestant.Builder().userJid("userJidB").status(ContestContestantStatus.APPROVED).build(),
                new ContestContestant.Builder().userJid("userJidA").status(ContestContestantStatus.APPROVED).build());

        assertThat(store.deleteContestant(contest.getJid(), "userJidA")).isTrue();
        assertThat(store.deleteContestant(contest.getJid(), "userJidC")).isFalse();

        contestantJids = store.getApprovedContestantJids(contest.getJid());
        assertThat(contestantJids).containsOnly("userJidB");
    }

    @Test
    void final_ranks_flow() {
        Contest contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contestA").build());
        Contest contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contestB").build());
        Contest contestC = contestStore.createContest(new ContestCreateData.Builder().slug("contestC").build());

        store.upsertContestant(contestA.getJid(), "userJidA");
        store.upsertContestant(contestB.getJid(), "userJidA");
        store.upsertContestant(contestC.getJid(), "userJidB");

        store.updateContestantFinalRank(contestA.getJid(), "userJidA", 2);
        store.updateContestantFinalRank(contestB.getJid(), "userJidA", 5);
        store.updateContestantFinalRank(contestC.getJid(), "userJidB", 1);

        assertThat(store.getContestantFinalRanks("userJidA")).isEqualTo(ImmutableMap.of(
                contestA.getJid(), 2,
                contestB.getJid(), 5));

        assertThat(store.getContestantFinalRanks("userJidB")).isEqualTo(ImmutableMap.of(
                contestC.getJid(), 1));
    }
}
