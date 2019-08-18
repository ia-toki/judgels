package judgels.uriel.contest.group;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.group.ContestGroupContest;
import judgels.uriel.persistence.ContestGroupContestDao;
import judgels.uriel.persistence.ContestGroupContestModel;

public class ContestGroupContestStore {
    private final ContestGroupContestDao contestDao;

    @Inject
    public ContestGroupContestStore(ContestGroupContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public void setContests(String contestGroupJid, List<ContestGroupContest> data) {
        Map<String, ContestGroupContest> setContests = data.stream().collect(
                Collectors.toMap(ContestGroupContest::getContestJid, Function.identity()));
        for (ContestGroupContestModel model : contestDao.selectAllByContestGroupJid(contestGroupJid, createOptions())) {
            ContestGroupContest existingContest = setContests.get(model.contestJid);
            if (existingContest == null || !existingContest.getAlias().equals(model.alias)) {
                contestDao.delete(model);
            }
        }

        for (ContestGroupContest contest : data) {
            Optional<ContestGroupContestModel> maybeModel =
                    contestDao.selectByContestGroupJidAndContestJid(contestGroupJid, contest.getContestJid());
            if (maybeModel.isPresent()) {
                ContestGroupContestModel model = maybeModel.get();
                model.alias = contest.getAlias();
                contestDao.update(model);
            } else {
                ContestGroupContestModel model = new ContestGroupContestModel();
                model.contestGroupJid = contestGroupJid;
                model.contestJid = contest.getContestJid();
                model.alias = contest.getAlias();
                contestDao.insert(model);
            }
        }
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
