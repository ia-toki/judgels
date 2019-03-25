package judgels.uriel.dump;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.persistence.ActorProvider;
import judgels.persistence.JudgelsModel;
import judgels.persistence.Model;
import judgels.persistence.UnmodifiableModel;
import judgels.persistence.api.dump.Dump;
import judgels.persistence.api.dump.JudgelsDump;
import judgels.persistence.api.dump.UnmodifiableDump;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import judgels.uriel.api.contest.module.ModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
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
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;

public class UrielDumpImporter {
    private final ActorProvider actorProvider;
    private final Clock clock;
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
    public UrielDumpImporter(
            ActorProvider actorProvider,
            Clock clock,
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

        this.actorProvider = actorProvider;
        this.clock = clock;
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

    public void importDump(UrielDump urielDump) {
        urielDump.getAdmins().forEach(dump -> importAdminRoleDump(dump));
        urielDump.getContests().forEach(dump -> importContestDump(dump));
    }

    public void importAdminRoleDump(AdminRoleDump adminRoleDump) {
        AdminRoleModel adminRoleModel = new AdminRoleModel();
        adminRoleModel.userJid = adminRoleDump.getUserJid();
        setCreationMetadata(adminRoleModel, adminRoleDump);
        adminRoleDao.persist(adminRoleModel);
    }

    public void importContestDump(ContestDump contestDump) {
        if (contestDao.selectByJid(contestDump.getJid()).isPresent()) {
            throw ContestErrors.jidAlreadyExists(contestDump.getJid());
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
        setJudgelsMetadata(contestModel, contestDump);
        contestDao.persist(contestModel);

        importStyleDump(contestDump.getJid(), contestDump.getStyle());

        contestDump.getModules().forEach(dump -> importModuleDump(contestDump.getJid(), dump));
        contestDump.getProblems().forEach(dump -> importProblemDump(contestDump.getJid(), dump));
        contestDump.getContestants().forEach(dump -> importContestantDump(contestDump.getJid(), dump));
        contestDump.getSupervisors().forEach(dump -> importSupervisorDump(contestDump.getJid(), dump));
        contestDump.getManagers().forEach(dump -> importManagerDump(contestDump.getJid(), dump));
        contestDump.getAnnouncements().forEach(dump -> importAnnouncementDump(contestDump.getJid(), dump));
        contestDump.getClarifications().forEach(dump -> importClarificationDump(contestDump.getJid(), dump));
    }

    public void importStyleDump(String contestJid, ContestStyleDump contestStyleDump) {
        String configString;
        try {
            StyleModuleConfig config = contestStyleDump.getConfig();
            configString = objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContestStyleModel contestStyleModel = new ContestStyleModel();
        contestStyleModel.contestJid = contestJid;
        contestStyleModel.config = configString;
        setCreationAndModificationMetadata(contestStyleModel, contestStyleDump);
        contestStyleDao.persist(contestStyleModel);
    }

    public void importModuleDump(String contestJid, ContestModuleDump contestModuleDump) {
        String configString;
        try {
            ModuleConfig config = contestModuleDump.getConfig();
            configString = objectMapper.writeValueAsString(config == null ? ImmutableMap.of() : config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContestModuleModel contestModuleModel = new ContestModuleModel();
        contestModuleModel.contestJid = contestJid;
        contestModuleModel.name = contestModuleDump.getName();
        contestModuleModel.enabled = contestModuleDump.getEnabled();
        contestModuleModel.config = configString;
        setCreationAndModificationMetadata(contestModuleModel, contestModuleDump);
        contestModuleDao.persist(contestModuleModel);
    }

    public void importProblemDump(String contestJid, ContestProblemDump contestProblemDump) {
        ContestProblemModel contestProblemModel = new ContestProblemModel();
        contestProblemModel.contestJid = contestJid;
        contestProblemModel.alias = contestProblemDump.getAlias();
        contestProblemModel.problemJid = contestProblemDump.getProblemJid();
        contestProblemModel.status = contestProblemDump.getStatus().name();
        contestProblemModel.submissionsLimit = contestProblemDump.getSubmissionsLimit();
        contestProblemModel.points = contestProblemDump.getPoints().orElse(0);
        setCreationAndModificationMetadata(contestProblemModel, contestProblemDump);
        contestProblemDao.persist(contestProblemModel);
    }

    public void importContestantDump(String contestJid, ContestContestantDump contestContestantDump) {
        String status = contestContestantDump.getStatus().orElse(ContestContestantStatus.APPROVED).name();

        ContestContestantModel contestContestantModel = new ContestContestantModel();
        contestContestantModel.contestJid = contestJid;
        contestContestantModel.userJid = contestContestantDump.getUserJid();
        contestContestantModel.status = status;
        contestContestantModel.contestStartTime = contestContestantDump.getContestStartTime().orElse(null);
        setCreationAndModificationMetadata(contestContestantModel, contestContestantDump);
        contestContestantDao.persist(contestContestantModel);
    }

    public void importSupervisorDump(String contestJid, ContestSupervisorDump contestSupervisorDump) {
        Set<SupervisorManagementPermission> managementPermissions = contestSupervisorDump.getManagementPermissions();
        SupervisorManagementPermissions permissions = managementPermissions.contains(SupervisorManagementPermission.ALL)
                ? SupervisorManagementPermissions.all()
                : SupervisorManagementPermissions.of(managementPermissions);

        String permissionsString;
        try {
            permissionsString = objectMapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContestSupervisorModel contestSupervisorModel = new ContestSupervisorModel();
        contestSupervisorModel.contestJid = contestJid;
        contestSupervisorModel.userJid = contestSupervisorDump.getUserJid();
        contestSupervisorModel.permission = permissionsString;
        setCreationAndModificationMetadata(contestSupervisorModel, contestSupervisorDump);
        contestSupervisorDao.persist(contestSupervisorModel);
    }

    public void importManagerDump(String contestJid, ContestManagerDump contestManagerDump) {
        ContestManagerModel contestManagerModel = new ContestManagerModel();
        contestManagerModel.contestJid = contestJid;
        contestManagerModel.userJid = contestManagerDump.getUserJid();
        setCreationAndModificationMetadata(contestManagerModel, contestManagerDump);
        contestManagerDao.persist(contestManagerModel);
    }

    public void importAnnouncementDump(String contestJid, ContestAnnouncementDump contestAnnouncementDump) {
        if (contestAnnouncementDao.selectByJid(contestAnnouncementDump.getJid()).isPresent()) {
            throw ContestErrors.jidAlreadyExists(contestAnnouncementDump.getJid());
        }

        ContestAnnouncementModel contestAnnouncementModel = new ContestAnnouncementModel();
        contestAnnouncementModel.contestJid = contestJid;
        contestAnnouncementModel.title = contestAnnouncementDump.getTitle();
        contestAnnouncementModel.content = contestAnnouncementDump.getContent();
        contestAnnouncementModel.status = contestAnnouncementDump.getStatus().name();
        setJudgelsMetadata(contestAnnouncementModel, contestAnnouncementDump);
        contestAnnouncementDao.persist(contestAnnouncementModel);
    }

    public void importClarificationDump(String contestJid, ContestClarificationDump contestClarificationDump) {
        if (contestClarificationDao.selectByJid(contestClarificationDump.getJid()).isPresent()) {
            throw ContestErrors.jidAlreadyExists(contestClarificationDump.getJid());
        }

        Optional<String> answer = contestClarificationDump.getAnswer();
        String status = answer.isPresent() && !answer.get().isEmpty()
                ? ContestClarificationStatus.ANSWERED.name()
                : ContestClarificationStatus.ASKED.name();

        ContestClarificationModel contestClarificationModel = new ContestClarificationModel();
        contestClarificationModel.contestJid = contestJid;
        contestClarificationModel.topicJid = contestClarificationDump.getTopicJid();
        contestClarificationModel.title = contestClarificationDump.getTitle();
        contestClarificationModel.question = contestClarificationDump.getQuestion();
        contestClarificationModel.answer = answer.orElse(null);
        contestClarificationModel.status = status;
        setJudgelsMetadata(contestClarificationModel, contestClarificationDump);
        contestClarificationDao.persist(contestClarificationModel);
    }

    private void setCreationMetadata(UnmodifiableModel model, UnmodifiableDump dump) {
        model.createdAt = dump.getCreatedAt();
        model.createdBy = dump.getCreatedBy();
        model.createdIp = dump.getCreatedIp();
    }

    private void setCreationAndModificationMetadata(Model model, Dump dump) {
        setCreationMetadata(model, dump);
        model.updatedAt = dump.getUpdatedAt();
        model.updatedBy = dump.getUpdatedBy();
        model.updatedIp = dump.getUpdatedIp();
    }

    private void setJudgelsMetadata(JudgelsModel model, JudgelsDump dump) {
        setCreationAndModificationMetadata(model, dump);
        model.jid = dump.getJid();
    }
}