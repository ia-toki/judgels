package judgels.uriel.contest.submission;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.persistence.api.SelectionOptions.DEFAULT_ALL;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.TestActorProvider;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.submission.SubmissionData;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.UrielIntegrationTestPersistenceModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestGradingModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestSubmissionModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestGradingModel.class, ContestSubmissionModel.class})
class ContestSubmissionStoreIntegrationTests {
    private TestActorProvider actorProvider;
    private ContestStore contestStore;
    private ContestSubmissionStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        actorProvider = new TestActorProvider();
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .urielIntegrationTestPersistenceModule(new UrielIntegrationTestPersistenceModule(actorProvider))
                .build();

        contestStore = component.contestStore();
        store = component.contestSubmissionStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        Contest contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        actorProvider.setJid("userJid1");

        Submission submission1 = store.createSubmission(new SubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        Submission submission2 = store.createSubmission(new SubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        actorProvider.setJid("userJid2");

        Submission submission3 = store.createSubmission(new SubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        Submission submission4 = store.createSubmission(new SubmissionData.Builder()
                .problemJid("problemJid2")
                .containerJid(contestA.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        actorProvider.setJid("userJid1");
        store.createSubmission(new SubmissionData.Builder()
                .problemJid("problemJid1")
                .containerJid(contestB.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        assertThat(store.getSubmissions(contestA.getJid(), empty(), empty(), empty(), DEFAULT_ALL).getData())
                .containsOnly(submission1, submission2, submission3, submission4);

        assertThat(store.getSubmissions(contestA.getJid(), of("userJid1"), empty(), empty(), DEFAULT_ALL).getData())
                .containsOnly(submission1, submission2);

        assertThat(store.getSubmissions(contestA.getJid(), of("userJid2"), empty(), empty(), DEFAULT_ALL).getData())
                .containsOnly(submission3, submission4);

        assertThat(store.getSubmissions(contestA.getJid(), empty(), of("problemJid1"), empty(), DEFAULT_ALL).getData())
                .containsOnly(submission1, submission2, submission3);

        assertThat(store.getSubmissions(contestA.getJid(), empty(), of("problemJid2"), empty(), DEFAULT_ALL).getData())
                .containsOnly(submission4);

        assertThat(store.getSubmissions(contestA.getJid(), of("userJid2"), of("problemJid1"), empty(), DEFAULT_ALL)
                .getData())
                .containsOnly(submission3);
    }
}
