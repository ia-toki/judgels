package judgels.uriel.contest.announcement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WithHibernateSession(models = {ContestModel.class, ContestAnnouncementModel.class})
class ContestAnnouncementStoreIntegrationTests extends AbstractIntegrationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestAnnouncementStoreIntegrationTests.class);

    private ContestStore contestStore;
    private ContestAnnouncementStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestAnnouncementStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

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

        Page<ContestAnnouncement> announcements = store.getPublishedAnnouncements(contest.getJid(), Optional.empty());
        assertThat(announcements.getPage()).containsExactly(announcement3, announcement1);

        announcements = store.getAnnouncements(contest.getJid(), Optional.empty());
        LOGGER.info("Checking flaky tests: {}", announcements);

        assertThat(announcements.getPage()).containsExactly(announcement3, announcement2, announcement1);

        announcement3 = store.updateAnnouncement(
                contest.getJid(),
                announcement3.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("Compiler version")
                        .content("g++ version is NOW 5.0.0")
                        .status(ContestAnnouncementStatus.PUBLISHED)
                        .build());

        announcements = store.getPublishedAnnouncements(contest.getJid(), Optional.empty());
        assertThat(announcements.getPage()).containsExactly(announcement3, announcement1);
    }
}
