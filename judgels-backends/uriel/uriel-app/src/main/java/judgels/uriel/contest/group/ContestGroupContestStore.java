package judgels.uriel.contest.group;

import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.ContestGroupContest;
import judgels.uriel.persistence.ContestGroupContestDao;
import judgels.uriel.persistence.ContestGroupContestModel;

public class ContestGroupContestStore {
    private final ContestGroupContestDao contestDao;

    @Inject
    public ContestGroupContestStore(ContestGroupContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public List<ContestGroupContest> getContests(String contestGroupJid) {
        return Lists.transform(
                contestDao.selectAllByContestGroupJid(contestGroupJid, createOptions()),
                ContestGroupContestStore::fromModel);
    }

    private static SelectionOptions createOptions() {
        return new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL)
                .orderBy("alias")
                .orderDir(OrderDir.ASC)
                .build();
    }

    private static ContestGroupContest fromModel(ContestGroupContestModel model) {
        return new ContestGroupContest.Builder()
                .contestJid(model.contestJid)
                .alias(model.alias)
                .build();
    }
}
