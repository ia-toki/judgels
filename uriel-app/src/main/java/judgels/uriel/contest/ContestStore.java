package judgels.uriel.contest;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.role.RoleStore;

public class ContestStore {
    private final RoleStore roleStore;
    private final ContestDao contestDao;

    @Inject
    public ContestStore(RoleStore roleStore, ContestDao contestDao) {
        this.roleStore = roleStore;
        this.contestDao = contestDao;
    }

    public Optional<Contest> findContestByJid(String contestJid) {
        return contestDao.selectByJid(contestJid).map(ContestStore::fromModel);
    }

    public Page<Contest> getContests(String userJid, SelectionOptions options) {
        Page<ContestModel> models;
        if (roleStore.isAdmin(userJid)) {
            models = contestDao.selectAll(options);
        } else {
            models = contestDao.selectAllByUserJid(userJid, options);
        }
        return models.mapData(data -> Lists.transform(data, ContestStore::fromModel));
    }

    public Contest createContest(ContestData contestData) {
        ContestModel model = new ContestModel();
        toModel(contestData, model);
        return fromModel(contestDao.insert(model));
    }

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .jid(model.jid)
                .name(model.name)
                .description(model.description)
                .style(ContestStyle.valueOf(model.style))
                .build();
    }

    private static void toModel(ContestData data, ContestModel model) {
        model.name = data.getName();
        model.description = data.getDescription();
        model.style = data.getStyle().name();
    }
}
