package judgels.uriel.contest.module;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;

@Singleton
public class ContestModuleStore {
    private final ContestModuleDao moduleDao;
    private final ObjectMapper mapper;

    private final Cache<String, Optional<?>> moduleCache;

    @Inject
    public ContestModuleStore(ContestModuleDao moduleDao, ObjectMapper mapper) {

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA " + getShortDuration());

        this.moduleDao = moduleDao;
        this.mapper = mapper;

        this.moduleCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build();
    }

    public void upsertClarificationModule(String contestJid) {
        upsertModule(contestJid, CLARIFICATION, Collections.emptyMap());
    }

    public void upsertClarificationTimeLimitModule(String contestJid, ClarificationTimeLimitModuleConfig config) {
        upsertModule(contestJid, CLARIFICATION_TIME_LIMIT, config);
    }

    public void upsertFrozenScoreboardModule(String contestJid, FrozenScoreboardModuleConfig config) {
        upsertModule(contestJid, FROZEN_SCOREBOARD, config);
    }

    public void upsertPausedModule(String contestJid) {
        upsertModule(contestJid, PAUSED, Collections.emptyMap());
    }

    public void upsertRegistrationModule(String contestJid) {
        upsertModule(contestJid, REGISTRATION, Collections.emptyMap());
    }

    public void upsertVirtualModule(String contestJid, VirtualModuleConfig config) {
        upsertModule(contestJid, VIRTUAL, config);
    }

    public boolean hasClarificationModule(String contestJid) {
        return moduleDao.selectByContestJidAndType(contestJid, CLARIFICATION).isPresent();
    }

    public Optional<ClarificationTimeLimitModuleConfig> getClarificationTimeLimitModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, CLARIFICATION_TIME_LIMIT, ClarificationTimeLimitModuleConfig.class);
    }

    public Optional<FrozenScoreboardModuleConfig> getFrozenScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.class);
    }

    public boolean hasPausedModule(String contestJid) {
        return moduleDao.selectByContestJidAndType(contestJid, PAUSED).isPresent();
    }

    public boolean hasRegistrationModule(String contestJid) {
        return moduleDao.selectByContestJidAndType(contestJid, REGISTRATION).isPresent();
    }


    public Optional<ScoreboardModuleConfig> getScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, SCOREBOARD, ScoreboardModuleConfig.class);
    }

    public Optional<VirtualModuleConfig> getVirtualModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, VIRTUAL, VirtualModuleConfig.class);
    }

    public void upsertModule(String contestJid, ContestModuleType type, Object config) {
        Optional<ContestModuleModel> maybeModel = moduleDao.selectByContestJidAndType(contestJid, type);
        if (maybeModel.isPresent()) {
            ContestModuleModel model = maybeModel.get();
            toModel(contestJid, type, config, model);
            moduleDao.update(model);
        } else {
            ContestModuleModel model = new ContestModuleModel();
            toModel(contestJid, type, config, model);
            moduleDao.insert(model);
        }
    }

    private void toModel(String contestJid, ContestModuleType type, Object config, ContestModuleModel model) {
        model.contestJid = contestJid;
        model.name = type.name();
        model.enabled = true;

        try {
            model.config = mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> getModuleConfig(String contestJid, ContestModuleType module, Class<T> configClass) {
        return (Optional<T>) moduleCache.get(
                contestJid + SEPARATOR + module.name(),
                $ -> getModuleConfigUncached(contestJid, module, configClass));
    }

    private <T> Optional<T> getModuleConfigUncached(String contestJid, ContestModuleType module, Class<T> configClass) {
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
