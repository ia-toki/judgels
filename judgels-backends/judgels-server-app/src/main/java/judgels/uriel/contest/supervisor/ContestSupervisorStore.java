package judgels.uriel.contest.supervisor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.dump.ContestSupervisorDump;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;

@Singleton
public class ContestSupervisorStore {
    private final ContestSupervisorDao supervisorDao;
    private final ObjectMapper mapper;

    @Inject
    public ContestSupervisorStore(ContestSupervisorDao supervisorDao, ObjectMapper mapper) {
        this.supervisorDao = supervisorDao;
        this.mapper = mapper;
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
        return supervisorDao.selectByContestJidAndUserJid(contestJid, userJid).map(this::fromModel);
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
        return supervisor;
    }

    public boolean deleteSupervisor(String contestJid, String userJid) {
        Optional<ContestSupervisorModel> maybeModel = supervisorDao.selectByContestJidAndUserJid(contestJid, userJid);
        if (!maybeModel.isPresent()) {
            return false;
        }
        supervisorDao.delete(maybeModel.get());
        return true;
    }

    public Page<ContestSupervisor> getSupervisors(String contestJid, int pageNumber, int pageSize) {
        return supervisorDao
                .selectByContestJid(contestJid)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, this::fromModel));
    }

    public Set<String> getAllSupervisorJids(String contestJid) {
        return supervisorDao
                .selectByContestJid(contestJid)
                .all()
                .stream()
                .map(model -> model.userJid)
                .collect(Collectors.toSet());
    }

    public void importDump(String contestJid, ContestSupervisorDump dump) {
        Set<SupervisorManagementPermission> managementPermissions = dump.getManagementPermissions();
        SupervisorManagementPermissions permissions = managementPermissions.contains(SupervisorManagementPermission.ALL)
                ? SupervisorManagementPermissions.all()
                : SupervisorManagementPermissions.of(managementPermissions);

        String permissionsString;
        try {
            permissionsString = mapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContestSupervisorModel model = new ContestSupervisorModel();
        model.contestJid = contestJid;
        model.userJid = dump.getUserJid();
        model.permission = permissionsString;
        supervisorDao.setModelMetadataFromDump(model, dump);
        supervisorDao.persist(model);
    }

    public Set<ContestSupervisorDump> exportDumps(String contestJid, DumpImportMode mode) {
        return supervisorDao.selectByContestJid(contestJid).all().stream().map(model -> {
            Set<SupervisorManagementPermission> permissions;
            try {
                SupervisorManagementPermissions permissionsWrapper = mapper.readValue(
                        model.permission,
                        SupervisorManagementPermissions.class);
                permissions = permissionsWrapper.getIsAllowedAll()
                        ? ImmutableSet.of(SupervisorManagementPermission.ALL)
                        : permissionsWrapper.getAllowedPermissions();
            } catch (IOException e) {
                throw new RuntimeException(
                        String.format(
                                "Failed to parse supervisor permissions JSON in contest %s:\n%s",
                                contestJid, model.permission
                        ),
                        e
                );
            }

            ContestSupervisorDump.Builder builder = new ContestSupervisorDump.Builder()
                    .mode(mode)
                    .userJid(model.userJid)
                    .managementPermissions(permissions);

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
