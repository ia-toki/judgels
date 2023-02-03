package judgels.uriel.contest.log;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestLogModel;

public class ContestLogStore {
    private final ContestLogDao dao;

    @Inject
    public ContestLogStore(ContestLogDao dao) {
        this.dao = dao;
    }

    public void createLog(ContestLog log) {
        ContestLogModel model = new ContestLogModel();
        model.contestJid = log.getContestJid();
        model.event = log.getEvent();
        model.object = log.getObject().orElse(null);
        model.problemJid = log.getProblemJid().orElse(null);
        model.createdBy = log.getUserJid();
        model.createdAt = log.getTime();
        model.createdIp = log.getIpAddress().orElse(null);

        dao.persist(model);
    }

    public Page<ContestLog> getLogs(
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        selectionOptions.orderBy("createdAt");
        selectionOptions.pageSize(100);
        page.ifPresent(selectionOptions::page);

        return dao.selectPaged(contestJid, userJid, problemJid, selectionOptions.build())
                .mapPage(models -> Lists.transform(models, this::fromModel));
    }

    private ContestLog fromModel(ContestLogModel model) {
        return new ContestLog.Builder()
                .contestJid(model.contestJid)
                .event(model.event)
                .object(Optional.ofNullable(model.object))
                .problemJid(Optional.ofNullable(model.problemJid))
                .userJid(model.createdBy)
                .time(model.createdAt)
                .ipAddress(model.createdIp)
                .build();
    }
}
