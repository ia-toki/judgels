package judgels.uriel.contest;

import static java.time.temporal.ChronoUnit.MILLIS;
import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.dump.ContestDump;
import judgels.uriel.api.contest.dump.ContestDumpComponent;
import judgels.uriel.api.contest.dump.ExportContestsDumpData;
import judgels.uriel.contest.announcement.ContestAnnouncementStore;
import judgels.uriel.contest.clarification.ContestClarificationStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;

@Singleton
public class ContestStore {
    private final ContestDao contestDao;

    private final ContestModuleStore moduleStore;
    private final ContestProblemStore problemStore;
    private final ContestContestantStore contestantStore;
    private final ContestSupervisorStore supervisorStore;
    private final ContestManagerStore managerStore;
    private final ContestAnnouncementStore announcementStore;
    private final ContestClarificationStore clarificationStore;

    private final LoadingCache<String, Contest> contestByJidCache;
    private final LoadingCache<String, Contest> contestBySlugCache;

    @Inject
    public ContestStore(
            ContestDao contestDao,
            ContestModuleStore moduleStore,
            ContestProblemStore problemStore,
            ContestContestantStore contestantStore,
            ContestSupervisorStore supervisorStore,
            ContestManagerStore managerStore,
            ContestAnnouncementStore announcementStore,
            ContestClarificationStore clarificationStore) {

        this.contestDao = contestDao;
        this.moduleStore = moduleStore;
        this.problemStore = problemStore;
        this.contestantStore = contestantStore;
        this.supervisorStore = supervisorStore;
        this.managerStore = managerStore;
        this.announcementStore = announcementStore;
        this.clarificationStore = clarificationStore;

        this.contestByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getContestByJidUncached);
        this.contestBySlugCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getContestBySlugUncached);
    }

    public Optional<Contest> getContestByJid(String contestJid) {
        return Optional.ofNullable(contestByJidCache.get(contestJid));
    }

    public Map<String, ContestInfo> getContestInfosByJids(Set<String> contestJids) {
        return contestDao.selectByJids(contestJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> new ContestInfo.Builder()
                                .slug(c.slug)
                                .name(c.name)
                                .beginTime(c.beginTime)
                                .build()));
    }

    private Contest getContestByJidUncached(String contestJid) {
        return contestDao.selectByJid(contestJid).map(ContestStore::fromModel).orElse(null);
    }

    public Optional<Contest> getContestBySlug(String contestSlug) {
        return Optional.ofNullable(contestBySlugCache.get(contestSlug));
    }

    private Contest getContestBySlugUncached(String contestSlug) {
        return contestDao.selectBySlug(contestSlug).map(ContestStore::fromModel).orElse(null);
    }

    public Page<Contest> getContests(String userJid, boolean isAdmin, Optional<String> name, Optional<Integer> page) {
        SearchOptions.Builder searchOptions = new SearchOptions.Builder();
        name.ifPresent(e -> searchOptions.putTerms("name", e));

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        selectionOptions.orderBy("beginTime");
        page.ifPresent(selectionOptions::page);

        Page<ContestModel> models = isAdmin
                ? contestDao.selectPaged(searchOptions.build(), selectionOptions.build())
                : contestDao.selectPagedByUserJid(userJid, searchOptions.build(), selectionOptions.build());
        return models.mapPage(p -> Lists.transform(p, ContestStore::fromModel));
    }

    public List<Contest> getActiveContests(String userJid, boolean isAdmin) {
        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy("beginTime")
                .orderDir(OrderDir.ASC)
                .build();

        List<ContestModel> models = isAdmin
                ? contestDao.selectAllActive(options)
                : contestDao.selectAllActiveByUserJid(userJid, options);
        return Lists.transform(models, ContestStore::fromModel);
    }

    public List<Contest> getRunningContests() {
        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy("beginTime")
                .orderDir(OrderDir.ASC)
                .build();

        return Lists.transform(contestDao.selectAllRunning(options), ContestStore::fromModel);
    }

    public List<Contest> getPubliclyParticipatedContests(String userJid) {
        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy("beginTime")
                .orderDir(OrderDir.ASC)
                .build();

        return Lists.transform(
                contestDao.selectAllPubliclyParticipatedByUserJid(userJid, options),
                ContestStore::fromModel);
    }

    public List<Contest> getPublicContestsAfter(Instant time) {
        return Lists.transform(
                contestDao.selectAllPublicAfter(time),
                ContestStore::fromModel);
    }

    public Contest createContest(ContestCreateData contestCreateData) {
        if (contestDao.selectBySlug(contestCreateData.getSlug()).isPresent()) {
            throw ContestErrors.slugAlreadyExists(contestCreateData.getSlug());
        }

        ContestModel model = new ContestModel();
        model.slug = contestCreateData.getSlug();
        model.name = contestCreateData.getSlug();
        model.description = "";
        model.style = ContestStyle.ICPC.name();
        model.beginTime = Instant.ofEpochSecond(4102444800L); // 1 January 2100
        model.duration = Duration.ofHours(5).toMillis();

        return fromModel(contestDao.insert(model));
    }

    public Optional<Contest> updateContest(String contestJid, ContestUpdateData contestUpdateData) {
        return contestDao.selectByJid(contestJid).map(model -> {
            if (contestUpdateData.getSlug().isPresent()) {
                String newSlug = contestUpdateData.getSlug().get();
                if (model.slug == null || !model.slug.equals(newSlug)) {
                    if (contestDao.selectBySlug(newSlug).isPresent()) {
                        throw ContestErrors.slugAlreadyExists(newSlug);
                    }
                }
            }

            contestByJidCache.invalidate(contestJid);
            if (model.slug != null) {
                contestBySlugCache.invalidate(model.slug);
            }
            if (contestUpdateData.getSlug().isPresent()) {
                contestBySlugCache.invalidate(contestUpdateData.getSlug().get());
            }

            contestUpdateData.getSlug().ifPresent(slug -> model.slug = slug);
            contestUpdateData.getName().ifPresent(name -> model.name = name);
            contestUpdateData.getStyle().ifPresent(style -> model.style = style.name());
            contestUpdateData.getBeginTime().ifPresent(time -> model.beginTime = time);
            contestUpdateData.getDuration().ifPresent(duration -> model.duration = duration.toMillis());
            return fromModel(contestDao.update(model));
        });
    }

    public Optional<String> getContestDescription(String contestJid) {
        return contestDao.selectByJid(contestJid).map(model -> model.description);
    }

    public Optional<ContestDescription> updateContestDescription(String contestJid, ContestDescription description) {
        return contestDao.selectByJid(contestJid).map(model -> {
            model.description = description.getDescription();
            contestDao.update(model);
            return description;
        });
    }

    public String importDump(ContestDump dump) {
        if (dump.getJid().isPresent() && contestDao.existsByJid(dump.getJid().get())) {
            throw ContestErrors.jidAlreadyExists(dump.getJid().get());
        }
        if (contestDao.selectBySlug(dump.getSlug()).isPresent()) {
            throw ContestErrors.slugAlreadyExists(dump.getSlug());
        }

        ContestModel model = new ContestModel();
        model.slug = dump.getSlug();
        model.name = dump.getName();
        model.style = dump.getStyle().getName().name();
        model.beginTime = dump.getBeginTime();
        model.duration = dump.getDuration().toMillis();
        model.description = dump.getDescription();
        contestDao.setModelMetadataFromDump(model, dump);
        model = contestDao.persist(model);
        contestByJidCache.invalidate(model.jid);
        contestBySlugCache.invalidate(model.slug);

        String contestJid = model.jid;
        moduleStore.importStyleDump(contestJid, dump.getStyle());

        dump.getModules().forEach(d -> moduleStore.importModuleDump(contestJid, d));
        dump.getProblems().forEach(d -> problemStore.importDump(contestJid, d));
        dump.getContestants().forEach(d -> contestantStore.importDump(contestJid, d));
        dump.getSupervisors().forEach(d -> supervisorStore.importDump(contestJid, d));
        dump.getManagers().forEach(d -> managerStore.importDump(contestJid, d));
        dump.getAnnouncements().forEach(d -> announcementStore.importDump(contestJid, d));
        dump.getClarifications().forEach(d -> clarificationStore.importDump(contestJid, d));

        return contestJid;
    }

    public Set<ContestDump> exportDumps(Map<String, ExportContestsDumpData.ContestDumpEntry> contestsToExport) {
        return contestDao.selectByJids(contestsToExport.keySet()).values().stream().map(model -> {
            DumpImportMode mode = contestsToExport.get(model.jid).getMode();
            Set<ContestDumpComponent> components = contestsToExport.get(model.jid).getComponents();

            ContestDump.Builder builder = new ContestDump.Builder()
                    .mode(mode)
                    .slug(model.slug)
                    .name(model.name)
                    .beginTime(model.beginTime)
                    .duration(Duration.ofMillis(model.duration))
                    .description(model.description)
                    .style(moduleStore.exportStyleDump(model.jid, mode, ContestStyle.valueOf(model.style)))
                    .modules(moduleStore.exportModuleDumps(model.jid, mode));

            if (components.contains(ContestDumpComponent.PROBLEMS)) {
                builder.problems(problemStore.exportDumps(model.jid, mode));
            }
            if (components.contains(ContestDumpComponent.CONTESTANTS)) {
                builder.contestants(contestantStore.exportDumps(model.jid, mode));
            }
            if (components.contains(ContestDumpComponent.SUPERVISORS)) {
                builder.supervisors(supervisorStore.exportDumps(model.jid, mode));
            }
            if (components.contains(ContestDumpComponent.MANAGERS)) {
                builder.managers(managerStore.exportDumps(model.jid, mode));
            }
            if (components.contains(ContestDumpComponent.ANNOUNCEMENTS)) {
                builder.announcements(announcementStore.exportDumps(model.jid, mode));
            }
            if (components.contains(ContestDumpComponent.CLARIFICATIONS)) {
                builder.clarifications(clarificationStore.exportDumps(model.jid, mode));
            }

            if (mode == DumpImportMode.RESTORE) {
                builder
                        .jid(model.jid)
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

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(Optional.ofNullable(model.slug).orElse("" + model.id))
                .name(model.name)
                .style(ContestStyle.valueOf(model.style))
                .beginTime(model.beginTime)
                .duration(Duration.of(model.duration, MILLIS))
                .build();
    }
}
