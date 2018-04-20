package judgels.uriel.contest;

import static java.time.temporal.ChronoUnit.MILLIS;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;

public class ContestStore {
    private final AdminRoleDao adminRoleDao;
    private final ContestDao contestDao;

    @Inject
    public ContestStore(AdminRoleDao adminRoleDao, ContestDao contestDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestDao = contestDao;
    }

    public Optional<Contest> findContestByJid(String contestJid) {
        return contestDao.selectByJid(contestJid).map(ContestStore::fromModel);
    }

    public Page<Contest> getContests(String userJid, SelectionOptions options) {
        Page<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectAll(options)
                : contestDao.selectAllByUserJid(userJid, options);
        return models.mapData(data -> Lists.transform(data, ContestStore::fromModel));
    }

    public List<Contest> getActiveContests(String userJid, SelectionOptions options) {
        List<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectAllActive(options)
                : contestDao.selectAllActiveByUserJid(userJid, options);
        return Lists.transform(models, ContestStore::fromModel);
    }

    public Page<Contest> getPastContests(String userJid, SelectionOptions options) {
        Page<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectAllPast(options)
                : contestDao.selectAllPastByUserJid(userJid, options);
        return models.mapData(data -> Lists.transform(data, ContestStore::fromModel));
    }

    public Contest createContest(ContestData contestData) {
        ContestModel model = new ContestModel();
        toModel(contestData, model);
        return fromModel(contestDao.insert(model));
    }

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .id(model.id)
                .jid(model.jid)
                .name(model.name)
                .description(model.description)
                .style(ContestStyle.valueOf(model.style))
                .beginTime(model.beginTime)
                .duration(Duration.of(model.duration, MILLIS))
                .build();
    }

    private static void toModel(ContestData data, ContestModel model) {
        model.name = data.getName();
        model.description = data.getDescription();
        model.style = data.getStyle().name();
        model.beginTime = data.getBeginTime();
        model.duration = data.getDuration().toMillis();
    }
}
