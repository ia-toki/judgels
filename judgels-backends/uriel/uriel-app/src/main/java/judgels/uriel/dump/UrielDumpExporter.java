package judgels.uriel.dump;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import judgels.uriel.api.contest.module.BundleStyleModuleConfig;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.DelayedGradingModuleConfig;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.GcjStyleModuleConfig;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import judgels.uriel.api.dump.AdminRoleDump;
import judgels.uriel.api.dump.ContestAnnouncementDump;
import judgels.uriel.api.dump.ContestClarificationDump;
import judgels.uriel.api.dump.ContestContestantDump;
import judgels.uriel.api.dump.ContestDump;
import judgels.uriel.api.dump.ContestManagerDump;
import judgels.uriel.api.dump.ContestModuleDump;
import judgels.uriel.api.dump.ContestProblemDump;
import judgels.uriel.api.dump.ContestStyleDump;
import judgels.uriel.api.dump.ContestSupervisorDump;
import judgels.uriel.api.dump.UrielDump;
import judgels.uriel.contest.supervisor.SupervisorManagementPermissions;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestSupervisorDao;

public class UrielDumpExporter {
    private final ObjectMapper objectMapper;
    private final AdminRoleDao adminRoleDao;
    private final ContestDao contestDao;
    private final ContestStyleDao contestStyleDao;
    private final ContestModuleDao contestModuleDao;
    private final ContestProblemDao contestProblemDao;
    private final ContestContestantDao contestContestantDao;
    private final ContestSupervisorDao contestSupervisorDao;
    private final ContestManagerDao contestManagerDao;
    private final ContestAnnouncementDao contestAnnouncementDao;
    private final ContestClarificationDao contestClarificationDao;

    @Inject
    public UrielDumpExporter(
            ObjectMapper objectMapper,
            AdminRoleDao adminRoleDao,
            ContestDao contestDao,
            ContestStyleDao contestStyleDao,
            ContestModuleDao contestModuleDao,
            ContestProblemDao contestProblemDao,
            ContestContestantDao contestContestantDao,
            ContestSupervisorDao contestSupervisorDao,
            ContestManagerDao contestManagerDao,
            ContestAnnouncementDao contestAnnouncementDao,
            ContestClarificationDao contestClarificationDao) {

        this.objectMapper = objectMapper;
        this.adminRoleDao = adminRoleDao;
        this.contestDao = contestDao;
        this.contestStyleDao = contestStyleDao;
        this.contestModuleDao = contestModuleDao;
        this.contestProblemDao = contestProblemDao;
        this.contestContestantDao = contestContestantDao;
        this.contestSupervisorDao = contestSupervisorDao;
        this.contestManagerDao = contestManagerDao;
        this.contestAnnouncementDao = contestAnnouncementDao;
        this.contestClarificationDao = contestClarificationDao;
    }

    public UrielDump exportDump() {
        return new UrielDump.Builder()
                .admins(exportAdminRoleDumps())
                .contests(exportContestDumps())
                .build();
    }

    public Set<AdminRoleDump> exportAdminRoleDumps() {
        return adminRoleDao.selectAll(SelectionOptions.DEFAULT_ALL).stream()
                .map(adminRoleModel -> new AdminRoleDump.Builder()
                        .userJid(adminRoleModel.userJid)
                        .createdAt(adminRoleModel.createdAt)
                        .createdBy(adminRoleModel.createdBy)
                        .createdIp(adminRoleModel.createdIp)
                        .build())
                .collect(Collectors.toSet());
    }

    public Set<ContestDump> exportContestDumps() {
        return contestDao.selectAll(SelectionOptions.DEFAULT_ALL).stream()
                .map(contestModel -> new ContestDump.Builder()
                        .slug(contestModel.slug)
                        .name(contestModel.name)
                        .beginTime(contestModel.beginTime)
                        .duration(Duration.ofMillis(contestModel.duration))
                        .description(contestModel.description)
                        .style(exportContestStyleDump(contestModel.jid, ContestStyle.valueOf(contestModel.style)))
                        .modules(exportContestModuleDumps(contestModel.jid))
                        .problems(exportContestProblemDumps(contestModel.jid))
                        .contestants(exportContestContestantDumps(contestModel.jid))
                        .supervisors(exportContestSupervisorDumps(contestModel.jid))
                        .managers(exportContestManagerDumps(contestModel.jid))
                        .announcements(exportContestAnnouncementDumps(contestModel.jid))
                        .clarifications(exportContestClarificationDumps(contestModel.jid))
                        .jid(contestModel.jid)
                        .createdAt(contestModel.createdAt)
                        .createdBy(contestModel.createdBy)
                        .createdIp(contestModel.createdIp)
                        .updatedAt(contestModel.updatedAt)
                        .updatedBy(contestModel.updatedBy)
                        .updatedIp(contestModel.updatedIp)
                        .build())
                .collect(Collectors.toSet());
    }

    public ContestStyleDump exportContestStyleDump(String contestJid, ContestStyle contestStyle) {
        Class<? extends StyleModuleConfig> styleModuleConfigClass;
        if (contestStyle == ContestStyle.IOI) {
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

        ContestStyleModel contestStyleModel = contestStyleDao.selectByContestJid(contestJid).get();
        try {
            return new ContestStyleDump.Builder()
                    .name(contestStyle)
                    .config(objectMapper.readValue(contestStyleModel.config, styleModuleConfigClass))
                    .createdAt(contestStyleModel.createdAt)
                    .createdBy(contestStyleModel.createdBy)
                    .createdIp(contestStyleModel.createdIp)
                    .updatedAt(contestStyleModel.updatedAt)
                    .updatedBy(contestStyleModel.updatedBy)
                    .updatedIp(contestStyleModel.updatedIp)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                        "Failed to parse style config JSON in contest %s:\n%s", contestJid, contestStyleModel.config
                    ),
                    e
            );
        }
    }

    public Set<ContestModuleDump> exportContestModuleDumps(String contestJid) {
        return contestModuleDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestModuleModel -> {
                    ContestModuleType moduleType = ContestModuleType.valueOf(contestModuleModel.name);
                    Optional<ModuleConfig> moduleConfig;
                    Class<? extends ModuleConfig> moduleConfigClass;
                    try {
                        if (moduleType == ContestModuleType.SCOREBOARD) {
                            moduleConfig = Optional.of(objectMapper.readValue(
                                    contestModuleModel.config, ScoreboardModuleConfig.class));
                        } else if (moduleType == ContestModuleType.CLARIFICATION_TIME_LIMIT) {
                            moduleConfig = Optional.of(objectMapper.readValue(
                                    contestModuleModel.config, ClarificationTimeLimitModuleConfig.class));
                        } else if (moduleType == ContestModuleType.DELAYED_GRADING) {
                            moduleConfig = Optional.of(objectMapper.readValue(
                                    contestModuleModel.config, DelayedGradingModuleConfig.class));
                        } else if (moduleType == ContestModuleType.FROZEN_SCOREBOARD) {
                            moduleConfig = Optional.of(objectMapper.readValue(
                                    contestModuleModel.config, FrozenScoreboardModuleConfig.class));
                        } else if (moduleType == ContestModuleType.VIRTUAL) {
                            moduleConfig = Optional.of(objectMapper.readValue(
                                    contestModuleModel.config, VirtualModuleConfig.class));
                        } else {
                            moduleConfig = Optional.empty();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(
                                String.format(
                                        "Failed to parse module config JSON in contest %s:\n%s",
                                        contestJid, contestModuleModel.config
                                ),
                                e
                        );
                    }

                    return new ContestModuleDump.Builder()
                            .name(ContestModuleType.valueOf(contestModuleModel.name))
                            .enabled(contestModuleModel.enabled)
                            .config(moduleConfig)
                            .createdAt(contestModuleModel.createdAt)
                            .createdBy(contestModuleModel.createdBy)
                            .createdIp(contestModuleModel.createdIp)
                            .updatedAt(contestModuleModel.updatedAt)
                            .updatedBy(contestModuleModel.updatedBy)
                            .updatedIp(contestModuleModel.updatedIp)
                            .build();
                })
                .collect(Collectors.toSet());
    }

    public Set<ContestProblemDump> exportContestProblemDumps(String contestJid) {
        return contestProblemDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestProblemModel -> new ContestProblemDump.Builder()
                        .alias(contestProblemModel.alias)
                        .problemJid(contestProblemModel.problemJid)
                        .status(ContestProblemStatus.valueOf(contestProblemModel.status))
                        .submissionsLimit(contestProblemModel.submissionsLimit)
                        .points(contestProblemModel.points)
                        .createdAt(contestProblemModel.createdAt)
                        .createdBy(contestProblemModel.createdBy)
                        .createdIp(contestProblemModel.createdIp)
                        .updatedAt(contestProblemModel.updatedAt)
                        .updatedBy(contestProblemModel.updatedBy)
                        .updatedIp(contestProblemModel.updatedIp)
                        .build())
                .collect(Collectors.toSet());
    }

    public Set<ContestContestantDump> exportContestContestantDumps(String contestJid) {
        return contestContestantDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestContestantModel -> new ContestContestantDump.Builder()
                        .userJid(contestContestantModel.userJid)
                        .status(ContestContestantStatus.valueOf(contestContestantModel.status))
                        .contestStartTime(contestContestantModel.contestStartTime)
                        .createdAt(contestContestantModel.createdAt)
                        .createdBy(contestContestantModel.createdBy)
                        .createdIp(contestContestantModel.createdIp)
                        .updatedAt(contestContestantModel.updatedAt)
                        .updatedBy(contestContestantModel.updatedBy)
                        .updatedIp(contestContestantModel.updatedIp)
                        .build())
                .collect(Collectors.toSet());
    }

    public Set<ContestSupervisorDump> exportContestSupervisorDumps(String contestJid) {
        return contestSupervisorDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestSupervisorModel -> {
                    Set<SupervisorManagementPermission> permissions;
                    try {
                        permissions = objectMapper.readValue(
                                contestSupervisorModel.permission,
                                SupervisorManagementPermissions.class
                        ).getAllowedPermissions();
                    } catch (IOException e) {
                        throw new RuntimeException(
                                String.format(
                                        "Failed to parse supervisor permissions JSON in contest %s:\n%s",
                                        contestJid, contestSupervisorModel.permission
                                ),
                                e
                        );
                    }

                    return new ContestSupervisorDump.Builder()
                            .userJid(contestSupervisorModel.userJid)
                            .managementPermissions(permissions)
                            .createdAt(contestSupervisorModel.createdAt)
                            .createdBy(contestSupervisorModel.createdBy)
                            .createdIp(contestSupervisorModel.createdIp)
                            .updatedAt(contestSupervisorModel.updatedAt)
                            .updatedBy(contestSupervisorModel.updatedBy)
                            .updatedIp(contestSupervisorModel.updatedIp)
                            .build();
                })
                .collect(Collectors.toSet());
    }

    public Set<ContestManagerDump> exportContestManagerDumps(String contestJid) {
        return contestManagerDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestManagerModel -> new ContestManagerDump.Builder()
                        .userJid(contestManagerModel.userJid)
                        .createdAt(contestManagerModel.createdAt)
                        .createdBy(contestManagerModel.createdBy)
                        .createdIp(contestManagerModel.createdIp)
                        .updatedAt(contestManagerModel.updatedAt)
                        .updatedBy(contestManagerModel.updatedBy)
                        .updatedIp(contestManagerModel.updatedIp)
                        .build())
                .collect(Collectors.toSet());
    }

    public Set<ContestAnnouncementDump> exportContestAnnouncementDumps(String contestJid) {
        return contestAnnouncementDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestAnnouncementModel -> new ContestAnnouncementDump.Builder()
                        .title(contestAnnouncementModel.title)
                        .content(contestAnnouncementModel.content)
                        .status(ContestAnnouncementStatus.valueOf(contestAnnouncementModel.status))
                        .jid(contestAnnouncementModel.jid)
                        .createdAt(contestAnnouncementModel.createdAt)
                        .createdBy(contestAnnouncementModel.createdBy)
                        .createdIp(contestAnnouncementModel.createdIp)
                        .updatedAt(contestAnnouncementModel.updatedAt)
                        .updatedBy(contestAnnouncementModel.updatedBy)
                        .updatedIp(contestAnnouncementModel.updatedIp)
                        .build())
                .collect(Collectors.toSet());
    }

    public Set<ContestClarificationDump> exportContestClarificationDumps(String contestJid) {
        return contestClarificationDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestClarificationModel -> new ContestClarificationDump.Builder()
                        .topicJid(contestClarificationModel.topicJid)
                        .title(contestClarificationModel.title)
                        .question(contestClarificationModel.question)
                        .answer(Optional.ofNullable(contestClarificationModel.answer))
                        .title(contestClarificationModel.title)
                        .jid(contestClarificationModel.jid)
                        .createdAt(contestClarificationModel.createdAt)
                        .createdBy(contestClarificationModel.createdBy)
                        .createdIp(contestClarificationModel.createdIp)
                        .updatedAt(contestClarificationModel.updatedAt)
                        .updatedBy(contestClarificationModel.updatedBy)
                        .updatedIp(contestClarificationModel.updatedIp)
                        .build())
                .collect(Collectors.toSet());
    }
}
