package judgels.uriel.contest.clarification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestClarificationModel.class})
class ContestClarificationStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestStore contestStore;
    private ContestClarificationStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestClarificationStore();
    }

    @Test
    void crud_flow() {
        Contest contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        Contest contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        ContestClarification clarification1 =
                store.createClarification(contestA.getJid(), new ContestClarificationData.Builder()
                    .topicJid(contestA.getJid())
                    .title("Snack")
                    .question("Is snack provided?")
                    .build());

        ContestClarification clarification2 =
                store.createClarification(contestA.getJid(), new ContestClarificationData.Builder()
                        .topicJid(contestA.getJid())
                        .title("Printing")
                        .question("Can we print?")
                        .build());

        ContestClarification clarification3 =
                store.createClarification(contestB.getJid(), new ContestClarificationData.Builder()
                        .topicJid(contestB.getJid())
                        .title("Balloon")
                        .question("No balloons?")
                        .build());

        assertThat(store.getClarifications(contestA.getJid(), Optional.empty()).getPage())
                .containsExactly(clarification2, clarification1);

        assertThat(clarification3.getAnswer()).isEmpty();
        assertThat(clarification3.getStatus()).isEqualTo(ContestClarificationStatus.ASKED);

        store.updateClarificationAnswer(contestB.getJid(), clarification3.getJid(), "Yes!");
        ContestClarification answeredClarification3 =
                store.getClarifications(contestB.getJid(), Optional.empty()).getPage().get(0);

        assertThat(answeredClarification3.getAnswer()).contains("Yes!");
        assertThat(answeredClarification3.getStatus()).isEqualTo(ContestClarificationStatus.ANSWERED);
    }
}
