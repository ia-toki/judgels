package judgels.uriel.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;

public class ContestModuleStore {
    private final ContestModuleDao moduleDao;
    private final ObjectMapper mapper;

    @Inject
    public ContestModuleStore(ContestModuleDao moduleDao, ObjectMapper mapper) {
        this.moduleDao = moduleDao;
        this.mapper = mapper;
    }

    public void upsertRegistrationModule(String contestJid) {
        upsertModule(contestJid, REGISTRATION, Collections.emptyMap());
    }

    public void upsertFrozenScoreboardModule(String contestJid, FrozenScoreboardModuleConfig config) {
        upsertModule(contestJid, FROZEN_SCOREBOARD, config);
    }

    public void upsertVirtualModule(String contestJid, VirtualModuleConfig config) {
        upsertModule(contestJid, VIRTUAL, config);
    }

    public Optional<FrozenScoreboardModuleConfig> getFrozenScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.class);
    }

    public Optional<ScoreboardModuleConfig> getScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, SCOREBOARD, ScoreboardModuleConfig.class);
    }

    public Optional<VirtualModuleConfig> getVirtualModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, VIRTUAL, VirtualModuleConfig.class);
    }

    public void upsertModule(String contestJid, ContestModuleType type, Object config) {
        ContestModuleModel model = new ContestModuleModel();
        model.contestJid = contestJid;
        model.name = type.name();
        model.enabled = true;

        try {
            model.config = mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        moduleDao.insert(model);
    }

    private <T> Optional<T> getModuleConfig(String contestJid, ContestModuleType module, Class<T> configClass) {
        return moduleDao.selectByContestJidAndType(contestJid, module)
                .map(model -> parseConfig(model.config, configClass));
    }

    private <T> T parseConfig(String config, Class<T> clazz) {
        try {
            return mapper.readValue(config, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
