package judgels.uriel.contest.module;

import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.EDITORIAL;
import static judgels.uriel.api.contest.module.ContestModuleType.EXTERNAL_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;
import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.MERGED_SCOREBOARD;
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
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.dump.ContestModuleDump;
import judgels.uriel.api.contest.dump.ContestStyleDump;
import judgels.uriel.api.contest.module.BundleStyleModuleConfig;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import judgels.uriel.api.contest.module.ExternalScoreboardModuleConfig;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.GcjStyleModuleConfig;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.MergedScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.module.TrocStyleModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;

@Singleton
public class ContestModuleStore {
    private static final Map<ContestModuleType, Object> DEFAULT_CONFIGS = Maps.immutableEnumMap(
            new ImmutableMap.Builder<ContestModuleType, Object>()
                    .put(CLARIFICATION_TIME_LIMIT, ClarificationTimeLimitModuleConfig.DEFAULT)
                    .put(DIVISION, DivisionModuleConfig.DEFAULT)
                    .put(EDITORIAL, EditorialModuleConfig.DEFAULT)
                    .put(EXTERNAL_SCOREBOARD, ExternalScoreboardModuleConfig.DEFAULT)
                    .put(FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.DEFAULT)
                    .put(MERGED_SCOREBOARD, MergedScoreboardModuleConfig.DEFAULT)
                    .put(SCOREBOARD, ScoreboardModuleConfig.DEFAULT)
                    .put(VIRTUAL, VirtualModuleConfig.DEFAULT)
                    .build());

    private static final Set<ContestModuleType> ALWAYS_ENABLED_MODULES = ImmutableSet.of(SCOREBOARD);

    private final ContestStyleDao styleDao; // TODO(fushar): put style config in module store as well
    private final ContestModuleDao moduleDao;
    private final ContestRoleDao roleDao;
    private final ObjectMapper mapper;

    private final Cache<String, Optional<?>> moduleCache;

    @Inject
    public ContestModuleStore(
            ContestStyleDao styleDao,
            ContestModuleDao moduleDao,
            ContestRoleDao roleDao,
            ObjectMapper mapper) {

        this.styleDao = styleDao;
        this.moduleDao = moduleDao;
        this.roleDao = roleDao;
        this.mapper = mapper;

        this.moduleCache = Caffeine.newBuilder()
                .maximumSize(100)
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
            roleDao.invalidateCaches();
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
            roleDao.invalidateCaches();
        }
    }

    public ContestModulesConfig getConfig(String contestJid, ContestStyle contestStyle) {
        ContestModulesConfig.Builder config = new ContestModulesConfig.Builder()
                .scoreboard(getScoreboardModuleConfig(contestJid))
                .clarificationTimeLimit(getClarificationTimeLimitModuleConfig(contestJid))
                .division(getDivisionModuleConfig(contestJid))
                .editorial(getEditorialModuleConfig(contestJid))
                .externalScoreboard(getExternalScoreboardModuleConfig(contestJid))
                .frozenScoreboard(getFrozenScoreboardModuleConfig(contestJid))
                .mergedScoreboard(getMergedScoreboardModuleConfig(contestJid))
                .virtual(getVirtualModuleConfig(contestJid));

        if (contestStyle == ContestStyle.TROC) {
            config.trocStyle(getTrocStyleModuleConfig(contestJid));
        } else if (contestStyle == ContestStyle.IOI) {
            config.ioiStyle(getIoiStyleModuleConfig(contestJid));
        } else if (contestStyle == ContestStyle.ICPC) {
            config.icpcStyle(getIcpcStyleModuleConfig(contestJid));
        } else if (contestStyle == ContestStyle.GCJ) {
            config.gcjStyle(getGcjStyleModuleConfig(contestJid));
        } else if (contestStyle == ContestStyle.BUNDLE) {
            config.bundleStyle(getBundleStyleModuleConfig(contestJid));
        } else {
            throw new IllegalArgumentException();
        }

        return config.build();
    }

    public void upsertConfig(String contestJid, ContestModulesConfig config) {
        config.getTrocStyle().ifPresent(c -> upsertTrocStyleModule(contestJid, c));
        config.getIcpcStyle().ifPresent(c -> upsertIcpcStyleModule(contestJid, c));
        config.getIoiStyle().ifPresent(c -> upsertIoiStyleModule(contestJid, c));
        config.getGcjStyle().ifPresent(c -> upsertGcjStyleModule(contestJid, c));
        config.getBundleStyle().ifPresent(c -> upsertBundleStyleModule(contestJid, c));

        upsertScoreboardModule(contestJid, config.getScoreboard());

        config.getClarificationTimeLimit().ifPresent(c -> upsertClarificationTimeLimitModule(contestJid, c));
        config.getDivision().ifPresent(c -> upsertDivisionModule(contestJid, c));
        config.getEditorial().ifPresent(c -> upsertEditorialModule(contestJid, c));
        config.getExternalScoreboard().ifPresent(c -> upsertExternalScoreboardModule(contestJid, c));
        config.getFrozenScoreboard().ifPresent(c -> upsertFrozenScoreboardModule(contestJid, c));
        config.getMergedScoreboard().ifPresent(c -> upsertMergedScoreboardModule(contestJid, c));
        config.getVirtual().ifPresent(c -> upsertVirtualModule(contestJid, c));
    }

    public void upsertTrocStyleModule(String contestJid, TrocStyleModuleConfig config) {
        upsertStyleConfig(contestJid, config);
    }

    public void upsertIoiStyleModule(String contestJid, IoiStyleModuleConfig config) {
        upsertStyleConfig(contestJid, config);
    }

    public void upsertIcpcStyleModule(String contestJid, IcpcStyleModuleConfig config) {
        upsertStyleConfig(contestJid, config);
    }

    public void upsertGcjStyleModule(String contestJid, GcjStyleModuleConfig config) {
        upsertStyleConfig(contestJid, config);
    }

    public void upsertBundleStyleModule(String contestJid, BundleStyleModuleConfig config) {
        upsertStyleConfig(contestJid, config);
    }

    public StyleModuleConfig getStyleModuleConfig(String contestJid, ContestStyle contestStyle) {
        if (contestStyle == ContestStyle.TROC) {
            return getTrocStyleModuleConfig(contestJid);
        } else if (contestStyle == ContestStyle.IOI) {
            return getIoiStyleModuleConfig(contestJid);
        } else if (contestStyle == ContestStyle.ICPC) {
            return getIcpcStyleModuleConfig(contestJid);
        } else if (contestStyle == ContestStyle.GCJ) {
            return getGcjStyleModuleConfig(contestJid);
        } else if (contestStyle == ContestStyle.BUNDLE) {
            return getBundleStyleModuleConfig(contestJid);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public TrocStyleModuleConfig getTrocStyleModuleConfig(String contestJid) {
        return getStyleConfig(contestJid, TrocStyleModuleConfig.class)
                .orElse(new TrocStyleModuleConfig.Builder().build());
    }

    public IcpcStyleModuleConfig getIcpcStyleModuleConfig(String contestJid) {
        return getStyleConfig(contestJid, IcpcStyleModuleConfig.class)
                .orElse(new IcpcStyleModuleConfig.Builder().build());
    }

    public IoiStyleModuleConfig getIoiStyleModuleConfig(String contestJid) {
        return getStyleConfig(contestJid, IoiStyleModuleConfig.class)
                .orElse(new IoiStyleModuleConfig.Builder().build());
    }

    public GcjStyleModuleConfig getGcjStyleModuleConfig(String contestJid) {
        return getStyleConfig(contestJid, GcjStyleModuleConfig.class)
                .orElse(new GcjStyleModuleConfig.Builder().build());
    }

    public BundleStyleModuleConfig getBundleStyleModuleConfig(String contestJid) {
        return getStyleConfig(contestJid, BundleStyleModuleConfig.class)
                .orElse(new BundleStyleModuleConfig.Builder().build());
    }

    public void upsertClarificationTimeLimitModule(String contestJid, ClarificationTimeLimitModuleConfig config) {
        upsertModule(contestJid, CLARIFICATION_TIME_LIMIT, config);
    }

    public void upsertDivisionModule(String contestJid, DivisionModuleConfig config) {
        upsertModule(contestJid, DIVISION, config);
    }

    public void upsertEditorialModule(String contestJid, EditorialModuleConfig config) {
        upsertModule(contestJid, EDITORIAL, config);
    }

    public void upsertExternalScoreboardModule(String contestJid, ExternalScoreboardModuleConfig config) {
        upsertModule(contestJid, EXTERNAL_SCOREBOARD, config);
    }

    public void upsertFileModule(String contestJid) {
        upsertModule(contestJid, FILE, Collections.emptyMap());
    }

    public void upsertFrozenScoreboardModule(String contestJid, FrozenScoreboardModuleConfig config) {
        upsertModule(contestJid, FROZEN_SCOREBOARD, config);
    }

    public void upsertMergedScoreboardModule(String contestJid, MergedScoreboardModuleConfig config) {
        upsertModule(contestJid, MERGED_SCOREBOARD, config);
    }

    public void upsertHiddenModule(String contestJid) {
        upsertModule(contestJid, HIDDEN, Collections.emptyMap());
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

    public void upsertScoreboardModule(String contestJid, ScoreboardModuleConfig config) {
        upsertModule(contestJid, SCOREBOARD, config);
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

    public Optional<DivisionModuleConfig> getDivisionModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, DIVISION, DivisionModuleConfig.class);
    }

    public Optional<EditorialModuleConfig> getEditorialModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, EDITORIAL, EditorialModuleConfig.class);
    }

    public Optional<ExternalScoreboardModuleConfig> getExternalScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, EXTERNAL_SCOREBOARD, ExternalScoreboardModuleConfig.class);
    }

    public boolean hasEditorialModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, EDITORIAL).isPresent();
    }

    public boolean hasFileModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, FILE).isPresent();
    }

    public Optional<FrozenScoreboardModuleConfig> getFrozenScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, FROZEN_SCOREBOARD, FrozenScoreboardModuleConfig.class);
    }

    public Optional<MergedScoreboardModuleConfig> getMergedScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, MERGED_SCOREBOARD, MergedScoreboardModuleConfig.class);
    }

    public boolean hasHiddenModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, HIDDEN).isPresent();
    }

    public boolean hasPausedModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, PAUSE).isPresent();
    }

    public boolean hasRegistrationModule(String contestJid) {
        return moduleDao.selectEnabledByContestJidAndType(contestJid, REGISTRATION).isPresent();
    }

    public ScoreboardModuleConfig getScoreboardModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, SCOREBOARD, ScoreboardModuleConfig.class)
                .orElse(ScoreboardModuleConfig.DEFAULT);
    }

    public Optional<VirtualModuleConfig> getVirtualModuleConfig(String contestJid) {
        return getModuleConfig(contestJid, VIRTUAL, VirtualModuleConfig.class);
    }

    public void importStyleDump(String contestJid, ContestStyleDump dump) {
        String configString;
        try {
            StyleModuleConfig config = dump.getConfig();
            configString = mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContestStyleModel model = new ContestStyleModel();
        model.contestJid = contestJid;
        model.config = configString;
        styleDao.setModelMetadataFromDump(model, dump);
        styleDao.persist(model);
    }

    public void importModuleDump(String contestJid, ContestModuleDump dump) {
        String configString;
        try {
            ModuleConfig config = dump.getConfig();
            configString = mapper.writeValueAsString(config == null ? ImmutableMap.of() : config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContestModuleModel model = new ContestModuleModel();
        model.contestJid = contestJid;
        model.name = dump.getName().name();
        model.enabled = dump.getEnabled();
        model.config = configString;
        moduleDao.setModelMetadataFromDump(model, dump);
        moduleDao.persist(model);
    }

    public ContestStyleDump exportStyleDump(String contestJid, DumpImportMode mode, ContestStyle contestStyle) {
        Class<? extends StyleModuleConfig> styleModuleConfigClass;
        if (contestStyle == ContestStyle.TROC) {
            styleModuleConfigClass = TrocStyleModuleConfig.class;
        } else if (contestStyle == ContestStyle.IOI) {
            styleModuleConfigClass = IoiStyleModuleConfig.class;
        } else if (contestStyle == ContestStyle.ICPC) {
            styleModuleConfigClass = IcpcStyleModuleConfig.class;
        } else if (contestStyle == ContestStyle.GCJ) {
            styleModuleConfigClass = GcjStyleModuleConfig.class;
        } else if (contestStyle == ContestStyle.BUNDLE) {
            styleModuleConfigClass = BundleStyleModuleConfig.class;
        } else {
            throw new IllegalArgumentException();
        }

        ContestStyleModel model = styleDao.selectByContestJid(contestJid).get();
        try {
            ContestStyleDump.Builder builder = new ContestStyleDump.Builder()
                    .mode(mode)
                    .name(contestStyle)
                    .config(mapper.readValue(model.config, styleModuleConfigClass));

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
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Failed to parse style config JSON in contest %s:\n%s", contestJid, model.config
                    ),
                    e
            );
        }
    }

    public Set<ContestModuleDump> exportModuleDumps(String contestJid, DumpImportMode mode) {
        return moduleDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream().map(model -> {
            ContestModuleType moduleType = ContestModuleType.valueOf(model.name);
            ModuleConfig moduleConfig = null;
            try {
                if (moduleType == ContestModuleType.SCOREBOARD) {
                    moduleConfig = mapper.readValue(model.config, ScoreboardModuleConfig.class);
                } else if (moduleType == ContestModuleType.CLARIFICATION_TIME_LIMIT) {
                    moduleConfig = mapper.readValue(model.config, ClarificationTimeLimitModuleConfig.class);
                } else if (moduleType == ContestModuleType.FROZEN_SCOREBOARD) {
                    moduleConfig = mapper.readValue(model.config, FrozenScoreboardModuleConfig.class);
                } else if (moduleType == ContestModuleType.VIRTUAL) {
                    moduleConfig = mapper.readValue(model.config, VirtualModuleConfig.class);
                }
            } catch (IOException e) {
                throw new RuntimeException(
                        String.format(
                                "Failed to parse module config JSON in contest %s:\n%s",
                                contestJid, model.config
                        ),
                        e
                );
            }

            ContestModuleDump.Builder builder = new ContestModuleDump.Builder()
                    .mode(mode)
                    .name(ContestModuleType.valueOf(model.name))
                    .enabled(model.enabled)
                    .config(moduleConfig);

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

    private void upsertModule(String contestJid, ContestModuleType type, Object config) {
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

        moduleCache.invalidate(contestJid + SEPARATOR + type.name());
        roleDao.invalidateCaches();
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

    private void upsertStyleConfig(String contestJid, Object config) {
        Optional<ContestStyleModel> maybeModel = styleDao.selectByContestJid(contestJid);
        if (maybeModel.isPresent()) {
            ContestStyleModel model = maybeModel.get();
            try {
                model.config = mapper.writeValueAsString(config);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            styleDao.update(model);
        } else {
            ContestStyleModel model = new ContestStyleModel();
            model.contestJid = contestJid;
            try {
                model.config = mapper.writeValueAsString(config);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            styleDao.insert(model);
        }
    }

    private <T> Optional<T> getStyleConfig(String contestJid, Class<T> configClass) {
        return styleDao.selectByContestJid(contestJid)
                .map(model -> parseConfig(model.config, configClass));
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
