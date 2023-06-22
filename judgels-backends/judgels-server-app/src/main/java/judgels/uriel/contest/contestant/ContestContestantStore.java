package judgels.uriel.contest.contestant;

import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;

import com.google.common.collect.Lists;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import judgels.uriel.api.contest.dump.ContestContestantDump;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;

@Singleton
public class ContestContestantStore {
    private final ContestContestantDao contestantDao;
    private final Clock clock;

    @Inject
    public ContestContestantStore(ContestContestantDao contestantDao, Clock clock) {
        this.contestantDao = contestantDao;
        this.clock = clock;
    }

    public boolean upsertContestant(String contestJid, String userJid) {
        Optional<ContestContestantModel> maybeModel = contestantDao.selectByContestJidAndUserJid(contestJid, userJid);
        if (maybeModel.isPresent()) {
            ContestContestantModel model = maybeModel.get();
            toModel(contestJid, userJid, model);
            contestantDao.update(model);
        } else {
            ContestContestantModel model = new ContestContestantModel();
            toModel(contestJid, userJid, model);
            contestantDao.insert(model);
        }
        return !maybeModel.isPresent();
    }

    public void updateContestantFinalRank(String contestJid, String userJid, int finalRank) {
        Optional<ContestContestantModel> maybeModel = contestantDao.selectByContestJidAndUserJid(contestJid, userJid);
        if (maybeModel.isPresent()) {
            ContestContestantModel model = maybeModel.get();
            model.finalRank = finalRank;
            contestantDao.update(model);
        }
    }

    public boolean deleteContestant(String contestJid, String userJid) {
        Optional<ContestContestantModel> maybeModel = contestantDao.selectByContestJidAndUserJid(contestJid, userJid);
        if (maybeModel.isPresent()) {
            contestantDao.delete(maybeModel.get());
            return true;
        }
        return false;
    }

    public Optional<ContestContestant> getContestant(String contestJid, String userJid) {
        return contestantDao.selectByContestJidAndUserJid(contestJid, userJid).map(ContestContestantStore::fromModel);
    }

    public Map<String, Integer> getContestantFinalRanks(String userJid) {
        return contestantDao
                .select()
                .whereUserParticipated(userJid)
                .all()
                .stream()
                .collect(Collectors.toMap(m -> m.contestJid, m -> m.finalRank));
    }

    public void startVirtualContest(String contestJid, String userJid) {
        ContestContestantModel model = contestantDao.selectByContestJidAndUserJid(contestJid, userJid).get();
        model.contestStartTime = clock.instant();
        contestantDao.update(model);
    }

    public void resetVirtualContest(String contestJid) {
        for (ContestContestantModel model : contestantDao.selectByContestJid(contestJid).all()) {
            model.contestStartTime = null;
            contestantDao.update(model);
        }
    }

    public Page<ContestContestant> getContestants(String contestJid, int pageNumber, int pageSize) {
        return contestantDao
                .selectByContestJid(contestJid)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestContestantStore::fromModel));
    }

    public int getApprovedContestantsCount(String contestJid) {
        return contestantDao.selectByContestJid(contestJid).count();
    }

    public Set<String> getApprovedContestantJids(String contestJid) {
        return contestantDao.selectByContestJid(contestJid)
                .all()
                .stream()
                .map(model -> model.userJid)
                .collect(Collectors.toSet());
    }

    public Set<ContestContestant> getApprovedContestants(String contestJid) {
        return contestantDao.selectByContestJid(contestJid)
                .all()
                .stream()
                .map(ContestContestantStore::fromModel)
                .collect(Collectors.toSet());
    }

    public void importDump(String contestJid, ContestContestantDump dump) {
        String status = dump.getStatus().orElse(ContestContestantStatus.APPROVED).name();

        ContestContestantModel model = new ContestContestantModel();
        model.contestJid = contestJid;
        model.userJid = dump.getUserJid();
        model.status = status;
        model.contestStartTime = dump.getContestStartTime().orElse(null);
        model.finalRank = dump.getFinalRank().orElse(null);
        contestantDao.setModelMetadataFromDump(model, dump);
        contestantDao.persist(model);
    }

    public Set<ContestContestantDump> exportDumps(String contestJid, DumpImportMode mode) {
        return contestantDao.selectByContestJid(contestJid).all().stream().map(model -> {
            ContestContestantDump.Builder builder = new ContestContestantDump.Builder()
                    .mode(mode)
                    .userJid(model.userJid)
                    .status(ContestContestantStatus.valueOf(model.status))
                    .contestStartTime(Optional.ofNullable(model.contestStartTime))
                    .finalRank(Optional.ofNullable(model.finalRank));

            if (mode == DumpImportMode.RESTORE) {
                builder
                        .createdAt(model.createdAt)
                        .createdBy(Optional.ofNullable(model.createdBy))
                        .createdIp(Optional.ofNullable(model.createdIp))
                        .updatedAt(model.updatedAt)
                        .updatedBy(Optional.ofNullable(model.updatedBy))
                        .updatedIp(Optional.ofNullable(model.updatedIp));
            }

            return builder.build();
        }).collect(Collectors.toSet());
    }

    private static void toModel(String contestJid, String userJid, ContestContestantModel model) {
        model.contestJid = contestJid;
        model.userJid = userJid;
        model.status = APPROVED.name();
    }

    private static ContestContestant fromModel(ContestContestantModel model) {
        return new ContestContestant.Builder()
                .userJid(model.userJid)
                .status(ContestContestantStatus.valueOf(model.status))
                .contestStartTime(Optional.ofNullable(model.contestStartTime))
                .build();
    }
}
