package judgels.uriel.contest.announcement;

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
        Contest contest = contestStore.createContest(new ContestData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestData.Builder().slug("contest-b").build());

        ContestAnnouncement announcement1 =
                store.createAnnouncement(contest.getJid(), new ContestAnnouncementData.Builder()
                        .title("Contest extension")
                        .content("The contest is extended by 30 mins")
                        .status(ContestAnnouncementStatus.PUBLISHED)
                        .build());
        ContestAnnouncement announcement2 =
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

        List<ContestAnnouncement> announcements = store.getPublishedAnnouncements(contest.getJid());
        assertThat(announcements).containsOnly(announcement3, announcement1);

        List<ContestAnnouncement> announcementsWithDraft = store.getAllAnnouncements(contest.getJid());
        assertThat(announcements).containsOnly(announcement3, announcement2, announcement1);
    }
}
