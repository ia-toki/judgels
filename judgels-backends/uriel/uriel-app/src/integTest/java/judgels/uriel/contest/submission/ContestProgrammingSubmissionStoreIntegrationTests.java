package judgels.uriel.contest.submission;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import judgels.persistence.TestActorProvider;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.sandalphon.api.submission.programming.ProgrammingSubmission;
import judgels.sandalphon.api.submission.programming.ProgrammingSubmissionData;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.submission.programming.ContestProgrammingSubmissionStore;
import judgels.uriel.persistence.ContestGradingModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestGradingModel.class, ContestProgrammingSubmissionModel.class})
class ContestProgrammingSubmissionStoreIntegrationTests extends AbstractIntegrationTests {
    private TestActorProvider actorProvider;
    private ContestStore contestStore;
    private ContestProgrammingSubmissionStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        actorProvider = new TestActorProvider();
        UrielIntegrationTestComponent component = createComponent(sessionFactory, actorProvider);

        contestStore = component.contestStore();
        store = component.contestSubmissionStore();
    }

    @Test
    void crud_flow() {
        Contest contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        Contest contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        actorProvider.setJid("userJid1");

        ProgrammingSubmission submission1 = store.createSubmission(new ProgrammingSubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        ProgrammingSubmission submission2 = store.createSubmission(new ProgrammingSubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        actorProvider.setJid("userJid2");

        ProgrammingSubmission submission3 = store.createSubmission(new ProgrammingSubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        ProgrammingSubmission submission4 = store.createSubmission(new ProgrammingSubmissionData.Builder()
                .problemJid("problemJid2")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        actorProvider.setJid("userJid1");
        store.createSubmission(new ProgrammingSubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestB.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        assertThat(store.getTotalSubmissions(contestA.getJid(), "userJid1", "problemJid1")).isEqualTo(2);
        assertThat(store.getTotalSubmissions(contestA.getJid(), "userJid1", "problemJid2")).isEqualTo(0);
        assertThat(store.getTotalSubmissionsMap(
                contestA.getJid(),
                "userJid2",
                ImmutableSet.of("problemJid1", "problemJid2", "problemJid3")))
                .isEqualTo(ImmutableMap.of("problemJid1", 1L, "problemJid2", 1L, "problemJid3", 0L));

        assertThat(store.getSubmissions(contestA.getJid(), empty(), empty(), empty()).getPage())
                .containsExactly(submission4, submission3, submission2, submission1);

        assertThat(store.getSubmissions(contestA.getJid(), of("userJid1"), empty(), empty()).getPage())
                .containsExactly(submission2, submission1);

        assertThat(store.getSubmissions(contestA.getJid(), of("userJid2"), empty(), empty()).getPage())
                .containsExactly(submission4, submission3);

        assertThat(store.getSubmissions(contestA.getJid(), empty(), of("problemJid1"), empty()).getPage())
                .containsExactly(submission3, submission2, submission1);

        assertThat(store.getSubmissions(contestA.getJid(), empty(), of("problemJid2"), empty()).getPage())
                .containsExactly(submission4);

        assertThat(store.getSubmissions(contestA.getJid(), of("userJid2"), of("problemJid1"), empty()).getPage())
                .containsExactly(submission3);
    }
}
