package judgels.uriel.contest.announcement;

import static judgels.uriel.UrielIntegrationTestPersistenceModule.ACTOR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestAnnouncementModel.class})
class ContestAnnouncementStoreIntegrationTests {
    private static final String USER = "userJid";

    private ContestStore contestStore;
    private ContestAnnouncementStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        contestStore = component.contestStore();
        store = component.contestAnnouncementStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder().name("contestA").build());
        contestStore.createContest(new ContestData.Builder().name("contestB").build());

        ContestAnnouncement announcement1 =
                store.createAnnouncement(contest.getJid(), new ContestAnnouncementData.Builder()
                        .title("Contest extension")
                        .content("The contest is extended by 30 mins")
                        .status(ContestAnnouncementStatus.PUBLISHED)
                        .build());
        store.createAnnouncement(contest.getJid(), new ContestAnnouncementData.Builder()
                .title("Snack")
                .content("Snack is available outside hall")
                .status(ContestAnnouncementStatus.DRAFT)
                .build());
        ContestAnnouncement announcement3 =
                store.createAnnouncement(contest.getJid(), new ContestAnnouncementData.Builder()
                        .title("Compiler version")
                        .content("g++ version is 4.9.2")
                        .status(ContestAnnouncementStatus.PUBLISHED)
                        .build());

        // TODO(fushar): move these assertions to service integration tests instead
        assertThat(announcement1.getUserJid()).isEqualTo(ACTOR);
        assertThat(announcement1.getTitle()).isEqualTo("Contest extension");
        assertThat(announcement1.getContent()).isEqualTo("The contest is extended by 30 mins");
        assertThat(announcement1.getStatus()).isEqualTo(ContestAnnouncementStatus.PUBLISHED);

        List<ContestAnnouncement> announcements = store.getAnnouncements(contest.getJid(), USER);
        assertThat(announcements).containsExactly(announcement3, announcement1);
    }
}
