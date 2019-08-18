package judgels.uriel.contest.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
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
class ContestGroupContestStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestStore contestStore;
    private ContestGroupStore contestGroupStore;
    private ContestGroupContestStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        contestGroupStore = component.contestGroupStore();
        store = component.contestGroupContestStore();
    }

    @Test
    void crud_flow() {
        Contest contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        Contest contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());
        ContestGroup contestGroup = contestGroupStore.createContestGroup(
                new ContestGroupCreateData.Builder().slug("contest-group").build());

        List<ContestGroupContest> contests = new ImmutableList.Builder<ContestGroupContest>()
                .add(new ContestGroupContest.Builder().contestJid(contestA.getJid()).alias("A").build())
                .add(new ContestGroupContest.Builder().contestJid(contestB.getJid()).alias("B").build())
                .build();
        store.setContests(contestGroup.getJid(), contests);

        assertThat(store.getContests(contestGroup.getJid())).isEqualTo(contests);
    }
}
