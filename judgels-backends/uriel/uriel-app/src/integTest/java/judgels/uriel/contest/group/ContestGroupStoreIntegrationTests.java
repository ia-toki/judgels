package judgels.uriel.contest.group;

import static judgels.uriel.api.contest.group.ContestGroupErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ServiceException;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.group.ContestGroup;
import judgels.uriel.api.contest.group.ContestGroupContest;
import judgels.uriel.api.contest.group.ContestGroupCreateData;
import judgels.uriel.contest.ContestGroupStore;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestGroupContestModel;
import judgels.uriel.persistence.ContestGroupModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestGroupModel.class, ContestGroupContestModel.class})
class ContestGroupStoreIntegrationTests  extends AbstractIntegrationTests {
    private ContestStore contestStore;
    private ContestGroupContestStore contestGroupContestStore;
    private ContestGroupStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        contestGroupContestStore = component.contestGroupContestStore();
        store = component.contestGroupStore();
    }

    @Test
    void crud_flow() {
        Contest contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        Contest contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());
        Contest contestC = contestStore.createContest(new ContestCreateData.Builder().slug("contest-c").build());

        ContestGroup contestGroupA =
                store.createContestGroup(new ContestGroupCreateData.Builder().slug("contest-group-a").build());
        ContestGroup contestGroupB =
                store.createContestGroup(new ContestGroupCreateData.Builder().slug("contest-group-b").build());
        ContestGroup contestGroupC =
                store.createContestGroup(new ContestGroupCreateData.Builder().slug("contest-group-c").build());

        assertThatThrownBy(
                () -> store.createContestGroup(new ContestGroupCreateData.Builder().slug("contest-group-c").build()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(SLUG_ALREADY_EXISTS.name());

        assertThat(contestGroupA.getSlug()).isEqualTo("contest-group-a");

        contestGroupContestStore.setContests(contestGroupA.getJid(), new ImmutableList.Builder<ContestGroupContest>()
                .add(new ContestGroupContest.Builder().contestJid(contestA.getJid()).alias("A").build())
                .build());

        contestGroupContestStore.setContests(contestGroupB.getJid(), new ImmutableList.Builder<ContestGroupContest>()
                .add(new ContestGroupContest.Builder().contestJid(contestB.getJid()).alias("B").build())
                .build());

        contestGroupContestStore.setContests(contestGroupC.getJid(), new ImmutableList.Builder<ContestGroupContest>()
                .add(new ContestGroupContest.Builder().contestJid(contestA.getJid()).alias("A").build())
                .add(new ContestGroupContest.Builder().contestJid(contestC.getJid()).alias("C").build())
                .build());

        assertThat(store.getContestGroupsByContestJids(ImmutableSet.of(contestB.getJid(), contestC.getJid())))
                .containsOnly(contestGroupB, contestGroupC);
    }
}
