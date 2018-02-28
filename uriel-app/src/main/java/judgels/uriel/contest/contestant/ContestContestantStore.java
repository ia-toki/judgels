package judgels.uriel.contest.contestant;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.contest.ContestDao;
import judgels.uriel.contest.ContestModel;

public class ContestContestantStore {
    private final ContestDao contestDao;
    private final ContestContestantDao contestContestantDao;

    @Inject
    public ContestContestantStore(ContestDao contestDao, ContestContestantDao contestContestantDao) {
        this.contestDao = contestDao;
        this.contestContestantDao = contestContestantDao;
    }

    public Optional<Page<String>> getContestantJids(String contestJid, int page, int pageSize) {
        Optional<ContestModel> maybeContestModel = contestDao.selectByJid(contestJid);
        if (!maybeContestModel.isPresent()) {
            return Optional.empty();
        } else {
            Page<ContestContestantModel> contestModelsPage =
                    contestContestantDao.selectAllByContestJid(contestJid, page, pageSize);

            return Optional.of(contestModelsPage.mapData(
                    data -> Lists.transform(data, ContestContestantStore::fromModel)));
        }
    }

    public Optional<Set<String>> addContestants(String contestJid, Set<String> contestantJids) {
        Optional<ContestModel> maybeContestModel = contestDao.selectByJid(contestJid);
        if (!maybeContestModel.isPresent()) {
            return Optional.empty();
        } else {
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

            return Optional.of(insertedContestantModels.stream()
                    .map(ContestContestantStore::fromModel)
                    .collect(Collectors.toSet()));
        }
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
