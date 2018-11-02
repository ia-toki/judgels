package judgels.uriel.contest.announcement;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
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

    public Page<ContestAnnouncement> getAnnouncements(String contestJid, Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        options.orderBy("updatedAt");
        page.ifPresent(options::page);
        return announcementDao.selectPagedByContestJid(contestJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestAnnouncementStore::fromModel));
    }

    public Page<ContestAnnouncement> getPublishedAnnouncements(String contestJid, Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        options.orderBy("updatedAt");
        page.ifPresent(options::page);
        return announcementDao.selectPagedPublishedByContestJid(contestJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestAnnouncementStore::fromModel));
    }

    public ContestAnnouncement updateAnnouncement(
            String contestJid,
            String announcementJid,
            ContestAnnouncementData data) {
        ContestAnnouncementModel model = announcementDao.selectByJid(announcementJid).get();
        toModel(contestJid, data, model);
        return fromModel(announcementDao.update(model));
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
