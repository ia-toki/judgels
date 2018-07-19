package judgels.uriel.contest.supervisor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Inject
    public ContestSupervisorStore(ContestSupervisorDao supervisorDao, ObjectMapper mapper) {
        this.supervisorDao = supervisorDao;
        this.mapper = mapper;
    }

    public Optional<ContestSupervisor> getSupervisor(String contestJid, String userJid) {
        return supervisorDao.selectByContestJidAndUserJid(contestJid, userJid)
                .map(this::fromModel);
    }

    public ContestSupervisor upsertSupervisor(String contestJid, ContestSupervisorData data) {
        Optional<ContestSupervisorModel> maybeModel =
                supervisorDao.selectByContestJidAndUserJid(contestJid, data.getUserJid());

        if (maybeModel.isPresent()) {
            ContestSupervisorModel model = maybeModel.get();
            toModel(contestJid, data, model);
            return fromModel(supervisorDao.update(model));
        } else {
            ContestSupervisorModel model = new ContestSupervisorModel();
            toModel(contestJid, data, model);
            return fromModel(supervisorDao.insert(model));
        }
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
