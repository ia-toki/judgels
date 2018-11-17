package judgels.uriel.contest.supervisor;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.ContestSupervisorData;
import judgels.uriel.api.contest.supervisor.SupervisorPermission;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;

public class ContestSupervisorStore {
    private final ContestSupervisorDao supervisorDao;
    private final ObjectMapper mapper;

    private final Cache<String, ContestSupervisor> supervisorCache;

    @Inject
    public ContestSupervisorStore(ContestSupervisorDao supervisorDao, ObjectMapper mapper) {
        this.supervisorDao = supervisorDao;
        this.mapper = mapper;

        this.supervisorCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(getShortDuration())
                .build();
    }

    public Optional<ContestSupervisor> getSupervisor(String contestJid, String userJid) {
        return Optional.ofNullable(supervisorCache.get(
                contestJid + SEPARATOR + userJid,
                $ -> getSupervisorUncached(contestJid, userJid)));
    }

    private ContestSupervisor getSupervisorUncached(String contestJid, String userJid) {
        return supervisorDao.selectByContestJidAndUserJid(contestJid, userJid)
                .map(this::fromModel)
                .orElse(null);
    }

    public ContestSupervisor upsertSupervisor(String contestJid, ContestSupervisorData data) {
        Optional<ContestSupervisorModel> maybeModel =
                supervisorDao.selectByContestJidAndUserJid(contestJid, data.getUserJid());

        ContestSupervisor supervisor;
        if (maybeModel.isPresent()) {
            ContestSupervisorModel model = maybeModel.get();
            toModel(contestJid, data, model);
            supervisor = fromModel(supervisorDao.update(model));
        } else {
            ContestSupervisorModel model = new ContestSupervisorModel();
            toModel(contestJid, data, model);
            supervisor = fromModel(supervisorDao.insert(model));
        }
        supervisorCache.invalidate(contestJid + SEPARATOR + data.getUserJid());
        return supervisor;
    }

    private ContestSupervisor fromModel(ContestSupervisorModel model) {
        SupervisorPermission permission;
        try {
            permission = mapper.readValue(model.permission, SupervisorPermission.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ContestSupervisor.Builder()
                .userJid(model.userJid)
                .permission(permission)
                .build();
    }

    private void toModel(
            String contestJid,
            ContestSupervisorData data,
            ContestSupervisorModel model) {

        model.contestJid = contestJid;
        model.userJid = data.getUserJid();

        try {
            model.permission = mapper.writeValueAsString(data.getPermission());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
