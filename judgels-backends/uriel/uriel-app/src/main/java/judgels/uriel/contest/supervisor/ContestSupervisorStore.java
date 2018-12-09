package judgels.uriel.contest.supervisor;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
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

    public boolean isSupervisorWithManagementPermission(
            String contestJid,
            String userJid,
            SupervisorManagementPermission permission) {

        Optional<ContestSupervisor> supervisor = getSupervisor(contestJid, userJid);
        if (!supervisor.isPresent()) {
            return false;
        }
        Set<SupervisorManagementPermission> permissions = supervisor.get().getManagementPermissions();
        return permissions.contains(SupervisorManagementPermission.ALL) || permissions.contains(permission);
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

    public ContestSupervisor upsertSupervisor(
            String contestJid,
            String userJid,
            Set<SupervisorManagementPermission> managementPermissions) {

        Optional<ContestSupervisorModel> maybeModel = supervisorDao.selectByContestJidAndUserJid(contestJid, userJid);

        ContestSupervisor supervisor;
        if (maybeModel.isPresent()) {
            ContestSupervisorModel model = maybeModel.get();
            toModel(contestJid, userJid, managementPermissions, model);
            supervisor = fromModel(supervisorDao.update(model));
        } else {
            ContestSupervisorModel model = new ContestSupervisorModel();
            toModel(contestJid, userJid, managementPermissions, model);
            supervisor = fromModel(supervisorDao.insert(model));
        }
        supervisorCache.invalidate(contestJid + SEPARATOR + userJid);
        return supervisor;
    }

    private ContestSupervisor fromModel(ContestSupervisorModel model) {
        SupervisorManagementPermissions permissions;
        try {
            permissions = mapper.readValue(model.permission, SupervisorManagementPermissions.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<SupervisorManagementPermission> managementPermissions = permissions.getIsAllowedAll()
                ? ImmutableSet.of(SupervisorManagementPermission.ALL)
                : permissions.getAllowedPermissions();

        return new ContestSupervisor.Builder()
                .userJid(model.userJid)
                .managementPermissions(managementPermissions)
                .build();
    }

    private void toModel(
            String contestJid,
            String userJid,
            Set<SupervisorManagementPermission> managementPermissions,
            ContestSupervisorModel model) {

        model.contestJid = contestJid;
        model.userJid = userJid;

        SupervisorManagementPermissions permissions = managementPermissions.contains(SupervisorManagementPermission.ALL)
                ? SupervisorManagementPermissions.all()
                : SupervisorManagementPermissions.of(managementPermissions);

        try {
            model.permission = mapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
