package judgels.uriel.contest.contestant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;

public class ContestContestantStore {
    private final ContestContestantDao contestantDao;

    @Inject
    public ContestContestantStore(ContestContestantDao contestantDao) {
        this.contestantDao = contestantDao;
    }

    // temporary
    public void upsertContestant(String contestJid, String userJid) {
        ContestContestantModel model = new ContestContestantModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        model.contestStartTime = Instant.ofEpochMilli(0);
        contestantDao.insert(model);
    }

    public Optional<ContestContestant> findContestant(String contestJid, String userJid) {
        return contestantDao.selectByContestJidAndUserJid(contestJid, userJid)
                .map(ContestContestantStore::fromModel);
    }

    public Page<String> getContestantJids(String contestJid, SelectionOptions options) {
        Page<ContestContestantModel> modelsPage = contestantDao.selectAllByContestJid(contestJid, options);
        return modelsPage.mapData(data -> Lists.transform(data, model -> model.userJid));
    }

    public List<String> addContestants(String contestJid, List<String> contestantJids) {
        List<String> userJidsToBeInserted = filterOutExistingUserJids(contestJid, contestantJids);
        List<ContestContestantModel> contestantsToBeInserted = userJidsToBeInserted.stream()
                .map(userJid -> {
                    ContestContestantModel contestantModel = new ContestContestantModel();
                    contestantModel.contestJid = contestJid;
                    contestantModel.userJid = userJid;
                    contestantModel.contestStartTime = Instant.ofEpochMilli(0);
                    return contestantModel;
                })
                .collect(Collectors.toList());

        List<ContestContestantModel> insertedContestantModels = contestantDao.insertAll(
                contestantsToBeInserted);

        return insertedContestantModels.stream()
                .map(model -> model.userJid)
                .collect(Collectors.toList());
    }

    private List<String> filterOutExistingUserJids(String contestJid, List<String> userJids) {
        List<String> existingJids = contestantDao.selectAllByContestJidAndUserJids(contestJid, userJids).stream()
                .map(contestant -> contestant.userJid)
                .collect(Collectors.toList());

        Set<String> userJidsToBeInserted = new HashSet<>(userJids);
        userJidsToBeInserted.removeAll(existingJids);

        return ImmutableList.copyOf(userJidsToBeInserted);
    }

    private static ContestContestant fromModel(ContestContestantModel model) {
        return new ContestContestant.Builder()
                .userJid(model.userJid)
                .contestStartTime(Optional.ofNullable(
                        model.contestStartTime.toEpochMilli() == 0 ? null : model.contestStartTime))
                .build();
    }
}
