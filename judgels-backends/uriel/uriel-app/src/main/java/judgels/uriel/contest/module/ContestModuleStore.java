package judgels.uriel.contest.module;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.DELAYED_GRADING;
import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.PAUSE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.DelayedGradingModuleConfig;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;

@Singleton
public class ContestModuleStore {
    private static final Map<ContestModuleType, Object> DEFAULT_CONFIGS = Maps.immutableEnumMap(
            new ImmutableMap.Builder<ContestModuleType, Object>()
                    .put(CLARIFICATION_TIME_LIMIT, ClarificationTimeLimitModuleConfig.DEFAULT)
                    .put(DELAYED_GRADING, DelayedGradingModuleConfig.DEFAULT)
                    .put(FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.DEFAULT)
                    .put(SCOREBOARD, ScoreboardModuleConfig.DEFAULT)
                    .put(VIRTUAL, VirtualModuleConfig.DEFAULT)
                    .build());

    private static final Set<ContestModuleType> ALWAYS_ENABLED_MODULES = ImmutableSet.of(SCOREBOARD);

    private final ContestModuleDao moduleDao;
    private final ObjectMapper mapper;

    private final Cache<String, Optional<?>> moduleCache;

    @Inject
    public ContestModuleStore(ContestModuleDao moduleDao, ObjectMapper mapper) {
        this.moduleDao = moduleDao;
        this.mapper = mapper;

        this.moduleCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build();
    }

    public Set<ContestModuleType> getEnabledModules(String contestJid) {
        return moduleDao.selectAllEnabledByContestJid(contestJid)
                .stream()
                .filter(model -> isValidModuleType(model.name))
                .map(model -> ContestModuleType.valueOf(model.name))
                .filter(type -> !ALWAYS_ENABLED_MODULES.contains(type))
                .collect(Collectors.toSet());
    }

    public void enableModule(String contestJid, ContestModuleType type) {
        Optional<ContestModuleModel> maybeModel = moduleDao.selectByContestJidAndType(contestJid, type);
        if (maybeModel.isPresent()) {
            ContestModuleModel model = maybeModel.get();
            model.enabled = true;
            moduleDao.update(model);
            moduleCache.invalidate(contestJid + SEPARATOR + type.name());
        } else {
            upsertModule(contestJid, type, DEFAULT_CONFIGS.getOrDefault(type, Collections.emptyMap()));
        }
    }

    public void disableModule(String contestJid, ContestModuleType type) {
        Optional<ContestModuleModel> maybeModel = moduleDao.selectByContestJidAndType(contestJid, type);
        if (maybeModel.isPresent()) {
            ContestModuleModel model = maybeModel.get();
            model.enabled = false;
            moduleDao.update(model);
            moduleCache.invalidate(contestJid + SEPARATOR + type.name());
        }
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
        upsertModule(contestJid, PAUSE, Collections.emptyMap());
    }

    public void disablePausedModule(String contestJid) {
        disableModule(contestJid, PAUSE);
    }

    public void upsertRegistrationModule(String contestJid) {
        upsertModule(contestJid, REGISTRATION, Collections.emptyMap());
    }

    public void upsertVirtualModule(String contestJid, VirtualModuleConfig config) {
        upsertModule(contestJid, VIRTUAL, config);
    }

    public boolean hasClarificationModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, CLARIFICATION).isPresent();
    }

    public Optional<ClarificationTimeLimitModuleConfig> getClarificationTimeLimitModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, CLARIFICATION_TIME_LIMIT, ClarificationTimeLimitModuleConfig.class);
    }

    public Optional<FrozenScoreboardModuleConfig> getFrozenScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.class);
    }

    public boolean hasPausedModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, PAUSE).isPresent();
    }

    public boolean hasRegistrationModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, REGISTRATION).isPresent();
    }

    public Optional<ScoreboardModuleConfig> getScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, SCOREBOARD, ScoreboardModuleConfig.class);
    }

    public Optional<VirtualModuleConfig> getVirtualModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, VIRTUAL, VirtualModuleConfig.class);
    }

    private void upsertModule(String contestJid, ContestModuleType type, Object config) {
        Optional<ContestModuleModel> maybeModel = moduleDao.selectEnabledByContestJidAndType(contestJid, type);
        if (maybeModel.isPresent()) {
            ContestModuleModel model = maybeModel.get();
            toModel(contestJid, type, config, model);
            moduleDao.update(model);
        } else {
            ContestModuleModel model = new ContestModuleModel();
            toModel(contestJid, type, config, model);
            moduleDao.insert(model);
        }

        moduleCache.invalidate(contestJid + SEPARATOR + type.name());
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
        return moduleDao.selectEnabledByContestJidAndType(contestJid, module)
                .map(model -> parseConfig(model.config, configClass));
    }

    private <T> T parseConfig(String config, Class<T> clazz) {
        try {
            return mapper.readValue(config, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidModuleType(String type) {
        try {
            ContestModuleType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
