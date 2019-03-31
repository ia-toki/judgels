package judgels.uriel.contest.announcement;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.api.dump.ContestAnnouncementDump;
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

    public void importDump(String contestJid, ContestAnnouncementDump contestAnnouncementDump) {
        if (contestAnnouncementDump.getJid().isPresent()
                && announcementDao.selectByJid(contestAnnouncementDump.getJid().get()).isPresent()) {
            throw ContestErrors.jidAlreadyExists(contestAnnouncementDump.getJid().get());
        }

        ContestAnnouncementModel contestAnnouncementModel = new ContestAnnouncementModel();
        contestAnnouncementModel.contestJid = contestJid;
        contestAnnouncementModel.title = contestAnnouncementDump.getTitle();
        contestAnnouncementModel.content = contestAnnouncementDump.getContent();
        contestAnnouncementModel.status = contestAnnouncementDump.getStatus().name();
        announcementDao.setModelMetadataFromDump(contestAnnouncementModel, contestAnnouncementDump);
        announcementDao.persist(contestAnnouncementModel);
    }

    public Set<ContestAnnouncementDump> exportDumps(String contestJid) {
        return announcementDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestAnnouncementModel -> new ContestAnnouncementDump.Builder()
                        .mode(DumpImportMode.RESTORE)
                        .title(contestAnnouncementModel.title)
                        .content(contestAnnouncementModel.content)
                        .status(ContestAnnouncementStatus.valueOf(contestAnnouncementModel.status))
                        .jid(contestAnnouncementModel.jid)
                        .createdAt(contestAnnouncementModel.createdAt)
                        .createdBy(Optional.ofNullable(contestAnnouncementModel.createdBy))
                        .createdIp(Optional.ofNullable(contestAnnouncementModel.createdIp))
                        .updatedAt(contestAnnouncementModel.updatedAt)
                        .updatedBy(Optional.ofNullable(contestAnnouncementModel.updatedBy))
                        .updatedIp(Optional.ofNullable(contestAnnouncementModel.updatedIp))
                        .build())
                .collect(Collectors.toSet());
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
