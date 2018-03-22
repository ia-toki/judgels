package judgels.uriel.contest.contestant;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.persistence.ContestContestantModel;

public class ContestContestantStore {
    private final ContestContestantDao contestContestantDao;

    @Inject
    public ContestContestantStore(ContestContestantDao contestContestantDao) {
        this.contestContestantDao = contestContestantDao;
    }

    public Page<String> getContestantJids(String contestJid, int page, int pageSize) {
        Page<ContestContestantModel> modelsPage =
                contestContestantDao.selectAllByContestJid(contestJid, page, pageSize);

        return modelsPage.mapData(data -> Lists.transform(data, ContestContestantStore::fromModel));
    }

    public Set<String> addContestants(String contestJid, Set<String> contestantJids) {
        Set<String> userJidsToBeInserted = filterOutExistingUserJids(contestJid, contestantJids);
        Set<ContestContestantModel> contestantsToBeInserted = userJidsToBeInserted.stream()
                .map(userJid -> {
                    ContestContestantModel contestantModel = new ContestContestantModel();
                    contestantModel.contestJid = contestJid;
                    contestantModel.userJid = userJid;
                    return contestantModel;
                })
                .collect(Collectors.toSet());

        Set<ContestContestantModel> insertedContestantModels = contestContestantDao.insertAll(
                contestJid,
                contestantsToBeInserted);

        return insertedContestantModels.stream()
                .map(ContestContestantStore::fromModel)
                .collect(Collectors.toSet());
    }

    private Set<String> filterOutExistingUserJids(String contestJid, Set<String> userJids) {
        List<String> existingJids = contestContestantDao.selectAllByUserJids(contestJid, userJids).stream()
                .map(contestant -> contestant.userJid)
                .collect(Collectors.toList());

        Set<String> userJidsToBeInserted = new HashSet<>(userJids);
        userJidsToBeInserted.removeAll(existingJids);

        return ImmutableSet.copyOf(userJidsToBeInserted);
    }

    private static String fromModel(ContestContestantModel model) {
        return model.userJid;
    }

}
