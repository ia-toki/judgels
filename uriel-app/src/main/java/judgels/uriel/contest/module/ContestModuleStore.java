package judgels.uriel.contest.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
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

    // temporary
    public void upsertModule(String contestJid, ContestModuleType module) {
        ContestModuleModel model = new ContestModuleModel();
        model.contestJid = contestJid;
        model.name = module.name();
        model.enabled = true;
        model.config = "{}";
        moduleDao.insert(model);
    }

    public Optional<FrozenScoreboardModuleConfig> getFrozenScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, ContestModuleType.FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.class);
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
