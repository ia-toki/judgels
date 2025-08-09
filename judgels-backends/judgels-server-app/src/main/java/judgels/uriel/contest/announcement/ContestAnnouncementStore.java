package judgels.uriel.contest.announcement;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestAnnouncementDao.ContestAnnouncementQueryBuilder;
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

    public Page<ContestAnnouncement> getAnnouncements(String contestJid, Optional<String> statusFilter, int pageNumber, int pageSize) {
        ContestAnnouncementQueryBuilder query = announcementDao.selectByContestJid(contestJid);

        if (statusFilter.isPresent()) {
            query.whereStatusIs(statusFilter.get());
        }

        return query
                .orderBy(Model_.UPDATED_AT, OrderDir.DESC)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestAnnouncementStore::fromModel));
    }

    public ContestAnnouncement updateAnnouncement(String contestJid, String announcementJid, ContestAnnouncementData data) {
        ContestAnnouncementModel model = announcementDao.findByJid(announcementJid);
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
