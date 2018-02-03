package judgels.uriel.contest;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;

public class ContestStore {
    private final ContestDao contestDao;

    @Inject
    public ContestStore(ContestDao contestDao) {
        this.contestDao = contestDao;
    }

    private static void toModel(ContestData data, ContestModel model) {
        model.name = data.getName();
    }

    public Optional<Contest> findContestByJid(String contestJid) {
        return contestDao.selectByJid(contestJid).map(this::fromModel);
    }

    public Contest createContest(ContestData contestData) {
        ContestModel model = new ContestModel();
        toModel(contestData, model);
        return fromModel(contestDao.insert(model));
    }

    private Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .jid(model.jid)
                .name(model.name)
                .build();
    }
}
