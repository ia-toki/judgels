package judgels.uriel.contest.problem;

import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestProblemModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestProblemModel.class})
class ContestProblemStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestProblemStore store;
    private ContestStore contestStore;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestProblemStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("another-contest").build());

        store.setProblems(contest.getJid(), ImmutableList.of(
                new ContestProblem.Builder()
                        .alias("C")
                        .problemJid("problemJid3")
                        .status(OPEN)
                        .submissionsLimit(50)
                        .points(11)
                        .build(),
                new ContestProblem.Builder()
                        .alias("A")
                        .problemJid("problemJid1")
                        .status(OPEN)
                        .points(23)
                        .build()));

        assertThat(store.hasClosedProblems(contest.getJid())).isFalse();

        List<ContestProblem> problems = ImmutableList.of(
                new ContestProblem.Builder()
                        .alias("C")
                        .problemJid("problemJid3")
                        .status(OPEN)
                        .submissionsLimit(50)
                        .points(11)
                        .build(),
                new ContestProblem.Builder()
                        .alias("A")
                        .problemJid("problemJid1")
                        .status(OPEN)
                        .points(23)
                        .build(),
                new ContestProblem.Builder()
                        .alias("B")
                        .problemJid("problemJid2")
                        .status(CLOSED)
                        .build());

        store.setProblems(contest.getJid(), problems);

        assertThat(store.hasClosedProblems(contest.getJid())).isTrue();

        assertThat(store.getProblemJids(contest.getJid())).containsOnly("problemJid1", "problemJid2", "problemJid3");
        assertThat(store.getOpenProblemJids(contest.getJid())).containsOnly("problemJid1", "problemJid3");
        assertThat(store.getProblemAliasesByJids(contest.getJid(), ImmutableSet.of("problemJid1", "problemJid2")))
                .isEqualTo(ImmutableMap.of("problemJid1", "A", "problemJid2", "B"));

        assertThat(store.getProblems(contest.getJid()))
                .containsExactly(problems.get(1), problems.get(2), problems.get(0));
    }
}
