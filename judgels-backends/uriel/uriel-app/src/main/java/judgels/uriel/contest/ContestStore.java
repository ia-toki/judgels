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
import judgels.uriel.api.dump.ContestDump;
import judgels.uriel.contest.announcement.ContestAnnouncementStore;
import judgels.uriel.contest.clarification.ContestClarificationStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;

@Singleton
public class ContestStore {
    private final AdminRoleDao adminRoleDao;
    private final ContestDao contestDao;

    private final ContestModuleStore contestModuleStore;
    private final ContestProblemStore contestProblemStore;
    private final ContestContestantStore contestContestantStore;
    private final ContestSupervisorStore contestSupervisorStore;
    private final ContestManagerStore contestManagerStore;
    private final ContestAnnouncementStore contestAnnouncementStore;
    private final ContestClarificationStore contestClarificationStore;

    private final LoadingCache<String, Contest> contestByJidCache;
    private final LoadingCache<String, Contest> contestBySlugCache;

    @Inject
    public ContestStore(
            AdminRoleDao adminRoleDao,
            ContestDao contestDao,
            ContestModuleStore contestModuleStore,
            ContestProblemStore contestProblemStore,
            ContestContestantStore contestContestantStore,
            ContestSupervisorStore contestSupervisorStore,
            ContestManagerStore contestManagerStore,
            ContestAnnouncementStore contestAnnouncementStore,
            ContestClarificationStore contestClarificationStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestDao = contestDao;
        this.contestModuleStore = contestModuleStore;
        this.contestProblemStore = contestProblemStore;
        this.contestContestantStore = contestContestantStore;
        this.contestSupervisorStore = contestSupervisorStore;
        this.contestManagerStore = contestManagerStore;
        this.contestAnnouncementStore = contestAnnouncementStore;
        this.contestClarificationStore = contestClarificationStore;

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

    public Page<Contest> getContests(String userJid, Optional<String> name, Optional<Integer> page) {
        SearchOptions.Builder searchOptions = new SearchOptions.Builder();
        name.ifPresent(e -> searchOptions.putTerms("name", e));

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        selectionOptions.orderBy("beginTime");
        page.ifPresent(selectionOptions::page);

        Page<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectPaged(searchOptions.build(), selectionOptions.build())
                : contestDao.selectPagedByUserJid(userJid, searchOptions.build(), selectionOptions.build());
        return models.mapPage(p -> Lists.transform(p, ContestStore::fromModel));
    }

    public List<Contest> getActiveContests(String userJid) {
        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy("beginTime")
                .orderDir(OrderDir.ASC)
                .build();

        List<ContestModel> models = adminRoleDao.isAdmin(userJid)
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

    public Optional<ContestDescription> getContestDescription(String contestJid) {
        return contestDao.selectByJid(contestJid).map(
                model -> new ContestDescription.Builder().description(model.description).build());
    }

    public Optional<ContestDescription> updateContestDescription(String contestJid, ContestDescription description) {
        return contestDao.selectByJid(contestJid).map(model -> {
            model.description = description.getDescription();
            contestDao.update(model);
            return description;
        });
    }

    public void importDump(ContestDump contestDump) {
        if (contestDump.getJid().isPresent() && contestDao.selectByJid(contestDump.getJid().get()).isPresent()) {
            throw ContestErrors.jidAlreadyExists(contestDump.getJid().get());
        }
        if (contestDao.selectBySlug(contestDump.getSlug()).isPresent()) {
            throw ContestErrors.slugAlreadyExists(contestDump.getSlug());
        }

        ContestModel contestModel = new ContestModel();
        contestModel.slug = contestDump.getSlug();
        contestModel.name = contestDump.getName();
        contestModel.style = contestDump.getStyle().getName().name();
        contestModel.beginTime = contestDump.getBeginTime();
        contestModel.duration = contestDump.getDuration().toMillis();
        contestModel.description = contestDump.getDescription();
        contestDao.setModelMetadataFromDump(contestModel, contestDump);
        contestModel = contestDao.persist(contestModel);
        contestByJidCache.invalidate(contestModel.jid);
        contestBySlugCache.invalidate(contestModel.slug);

        String contestJid = contestModel.jid;
        contestModuleStore.importStyleDump(contestJid, contestDump.getStyle());

        contestDump.getModules().forEach(dump -> contestModuleStore.importModuleDump(contestJid, dump));
        contestDump.getProblems().forEach(dump -> contestProblemStore.importDump(contestJid, dump));
        contestDump.getContestants().forEach(dump -> contestContestantStore.importDump(contestJid, dump));
        contestDump.getSupervisors().forEach(dump -> contestSupervisorStore.importDump(contestJid, dump));
        contestDump.getManagers().forEach(dump -> contestManagerStore.importDump(contestJid, dump));
        contestDump.getAnnouncements().forEach(dump -> contestAnnouncementStore.importDump(contestJid, dump));
        contestDump.getClarifications().forEach(dump -> contestClarificationStore.importDump(contestJid, dump));
    }

    public Set<ContestDump> exportDumps() {
        return contestDao.selectAll(SelectionOptions.DEFAULT_ALL).stream()
                .map(contestModel -> new ContestDump.Builder()
                        .mode(DumpImportMode.RESTORE)
                        .slug(contestModel.slug)
                        .name(contestModel.name)
                        .beginTime(contestModel.beginTime)
                        .duration(Duration.ofMillis(contestModel.duration))
                        .description(contestModel.description)
                        .style(contestModuleStore.exportStyleDump(
                                contestModel.jid, ContestStyle.valueOf(contestModel.style)))
                        .modules(contestModuleStore.exportModuleDumps(contestModel.jid))
                        .problems(contestProblemStore.exportDumps(contestModel.jid))
                        .contestants(contestContestantStore.exportDumps(contestModel.jid))
                        .supervisors(contestSupervisorStore.exportDumps(contestModel.jid))
                        .managers(contestManagerStore.exportDumps(contestModel.jid))
                        .announcements(contestAnnouncementStore.exportDumps(contestModel.jid))
                        .clarifications(contestClarificationStore.exportDumps(contestModel.jid))
                        .jid(contestModel.jid)
                        .createdAt(contestModel.createdAt)
                        .createdBy(Optional.ofNullable(contestModel.createdBy))
                        .createdIp(Optional.ofNullable(contestModel.createdIp))
                        .updatedAt(contestModel.updatedAt)
                        .updatedBy(Optional.ofNullable(contestModel.updatedBy))
                        .updatedIp(Optional.ofNullable(contestModel.updatedIp))
                        .build())
                .collect(Collectors.toSet());
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
