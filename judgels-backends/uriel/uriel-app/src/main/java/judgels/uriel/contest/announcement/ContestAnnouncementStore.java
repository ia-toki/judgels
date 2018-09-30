package judgels.uriel.contest.announcement;

import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestAnnouncementModel;

public class ContestAnnouncementStore {
    private final ContestAnnouncementDao announcementDao;

    @Inject
    public ContestAnnouncementStore(ContestAnnouncementDao announcementDao) {
        this.announcementDao = announcementDao;
    }

    public ContestAnnouncement createAnnouncement(String contestJid, ContestAnnouncementData data) {
        ContestAnnouncementModel model = new ContestAnnouncementModel();
        toModel(contestJid, data, model);
        return fromModel(announcementDao.insert(model));
    }

    public List<ContestAnnouncement> getAnnouncements(String contestJid) {
        return Lists.transform(
                announcementDao.selectAllByContestJid(contestJid),
                ContestAnnouncementStore::fromModel);
    }

    public List<ContestAnnouncement> getPublishedAnnouncements(String contestJid) {
        return Lists.transform(
                announcementDao.selectAllPublishedByContestJid(contestJid),
                ContestAnnouncementStore::fromModel);
    }

    private static ContestAnnouncement fromModel(ContestAnnouncementModel model) {
        return new ContestAnnouncement.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .title(model.title)
                .content(model.content)
                .status(ContestAnnouncementStatus.valueOf(model.status))
                .updatedTime(model.updatedAt)
                .build();
    }

    private static void toModel(String contestJid, ContestAnnouncementData data, ContestAnnouncementModel model) {
        model.contestJid = contestJid;
        model.title = data.getTitle();
        model.content = data.getContent();
        model.status = data.getStatus().name();
    }
}
