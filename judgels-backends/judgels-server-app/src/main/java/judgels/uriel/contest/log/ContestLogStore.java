package judgels.uriel.contest.log;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestLogDao.ContestLogQueryBuilder;
import judgels.uriel.persistence.ContestLogModel;

public class ContestLogStore {
    private final ContestLogDao logDao;

    @Inject
    public ContestLogStore(ContestLogDao logDao) {
        this.logDao = logDao;
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

        logDao.persist(model);
    }

    public Page<ContestLog> getLogs(
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            int pageNumber,
            int pageSize) {

        ContestLogQueryBuilder query = logDao.selectByContestJid(contestJid);

        if (userJid.isPresent()) {
            query.whereUserIs(userJid.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }

        return query
                .orderBy(UnmodifiableModel_.CREATED_AT, OrderDir.DESC)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestLogStore::fromModel));
    }

    private static ContestLog fromModel(ContestLogModel model) {
        return new ContestLog.Builder()
                .contestJid(model.contestJid)
                .event(model.event)
                .object(Optional.ofNullable(model.object))
                .problemJid(Optional.ofNullable(model.problemJid))
                .userJid(model.createdBy)
                .time(model.createdAt)
                .ipAddress(Optional.ofNullable(model.createdIp))
                .build();
    }
}
