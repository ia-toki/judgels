package judgels.uriel.contest.contestant;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestRoleDao;

@Singleton
public class ContestContestantStore {
    private final ContestContestantDao contestantDao;
    private final ContestRoleDao roleDao;
    private final Clock clock;

    private final Cache<String, ContestContestant> contestantCache;

    @Inject
    public ContestContestantStore(ContestContestantDao contestantDao, ContestRoleDao roleDao, Clock clock) {
        this.contestantDao = contestantDao;
        this.roleDao = roleDao;
        this.clock = clock;

        this.contestantCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(getShortDuration())
                .build();
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

        contestantCache.invalidate(contestJid + SEPARATOR + userJid);
        roleDao.invalidateCaches(userJid, contestJid);

        return !maybeModel.isPresent();
    }

    public void removeContestant(String contestJid, String userJid) {
        contestantDao.selectByContestJidAndUserJid(contestJid, userJid).ifPresent(contestantDao::delete);

        contestantCache.invalidate(contestJid + SEPARATOR + userJid);
        roleDao.invalidateCaches(userJid, contestJid);
    }

    public Optional<ContestContestant> getContestant(String contestJid, String userJid) {
        return Optional.ofNullable(contestantCache.get(
                contestJid + SEPARATOR + userJid,
                $ -> getContestantUncached(contestJid, userJid)));
    }

    private ContestContestant getContestantUncached(String contestJid, String userJid) {
        return contestantDao.selectByContestJidAndUserJid(contestJid, userJid)
                .map(ContestContestantStore::fromModel)
                .orElse(null);
    }

    public void startVirtualContest(String contestJid, String userJid) {
        contestantDao.selectByContestJidAndUserJid(contestJid, userJid).ifPresent(model -> {
            model.contestStartTime = clock.instant();
            contestantDao.update(model);

            contestantCache.invalidate(contestJid + SEPARATOR + userJid);
            roleDao.invalidateCaches(userJid, contestJid);
        });
    }

    public Page<ContestContestant> getContestants(String contestJid, Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);
        return contestantDao.selectPagedByContestJid(contestJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestContestantStore::fromModel));
    }

    public long getApprovedContestantsCount(String contestJid) {
        return contestantDao.selectCountApprovedByContestJid(contestJid);
    }

    public Set<String> getApprovedContestantJids(String contestJid) {
        return contestantDao.selectAllApprovedByContestJid(contestJid)
                .stream()
                .map(model -> model.userJid)
                .collect(Collectors.toSet());
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
